import { catalogs, localeCodes } from "./locale-catalogs.ts";
import templates from "./message-templates.json";

const root: HTMLElement = document.documentElement;
const rtl: Set<string> = new Set(["ar", "he", "ur", "fa", "sd", "ckb", "ps", "ug", "yi"]);
const aliases: Record<string, string> = {
  nb: "no",
  nn: "no",
  iw: "he",
  in: "id",
  tl: "fil",
  ay: "ayr",
};
function read(key: string): string | null {
  try {
    return localStorage.getItem(key);
  } catch {
    return null;
  }
}
function save(key: string, value: string): void {
  try {
    localStorage.setItem(key, value);
  } catch {}
}
function resolveLocale(value: string): string | undefined {
  const base: string = value.toLowerCase().split("-")[0] ?? "en";
  return localeCodes.includes(base) ? base : aliases[base];
}
let locale: string =
  resolveLocale(read("seng-locale") ?? "") ??
  navigator.languages.map(resolveLocale).find(Boolean) ??
  "en";
const rules: { source: string; pattern: RegExp }[] = templates.map(
  (source: string): { source: string; pattern: RegExp } => ({
    source,
    pattern: new RegExp(
      `^${source
        .split(/\{\d+}/)
        .map((part): string => part.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"))
        .join("(.+?)")}$`,
    ),
  }),
);
function translate(value: string, dynamic = false): string {
  const key: string = value.trim();
  const catalog: Record<string, string> = catalogs[locale] ?? catalogs.en ?? {};
  const direct: string | undefined = catalog[key];
  if (direct) return value.replace(key, (): string => direct);
  if (dynamic)
    for (const rule of rules) {
      const match: RegExpMatchArray | null = key.match(rule.pattern);
      if (match) {
        const translated: string = (catalog[rule.source] ?? rule.source).replace(
          /\{(\d+)}/g,
          (_, index: string): string => {
            const part: string = match[Number(index) + 1] ?? "";
            const duration: RegExpExecArray | null = /^(\d+) days · (\d+) hours · (\d+) min$/.exec(
              part,
            );
            if (!duration) return part;
            return (
              catalog["{0} days · {1} hours · {2} min"] ?? "{0} days · {1} hours · {2} min"
            ).replace(
              /\{(\d+)}/g,
              (_: string, position: string): string => duration[Number(position) + 1] ?? "",
            );
          },
        );
        return value.replace(key, (): string => translated);
      }
    }
  return value;
}
function element<T extends HTMLElement>(id: string): T {
  const node: HTMLElement | null = document.getElementById(id);
  if (!node) throw new Error(`Missing preference control: ${id}`);
  return node as T;
}
const excluded: string =
  "script,style,svg,textarea,[translate='no'],#languageOptions,wa-button,wa-badge,[data-feedback-source],#ownerNameDisplay,#ownerEmailDisplay,.vehicle-plate,.vehicle-name,#vehicleEditorSubtitle";
const dynamic: string =
  "#message,#registryMessage,#registryAuthMessage,#registrationStatusTitle,#gateSelectedFree,.free-count,#quoteDuration,#registrationStatusDetail";
interface Source {
  original: string;
  rendered: string;
}
const textSources: WeakMap<Text, Source> = new WeakMap<Text, Source>();
const attributeSources: WeakMap<Element, Map<string, Source>> = new WeakMap<
  Element,
  Map<string, Source>
>();

export function sourceText(node: Node): string {
  if (node instanceof Text) {
    const source: Source | undefined = textSources.get(node);
    return source?.rendered === node.data ? source.original : node.data;
  }
  return [...node.childNodes].map(sourceText).join("");
}

function render(node: Node): void {
  if (node instanceof Text) {
    const parent: HTMLElement | null = node.parentElement;
    if (!parent || parent.closest(excluded)) return;
    if (parent.closest("#registrySessionBadge") && node.data.includes("@")) return;
    const previous: Source | undefined = textSources.get(node);
    const original: string = previous?.rendered === node.data ? previous.original : node.data;
    const record: Element | null = parent.closest(
      ".vehicle-meta, #registrationStatus.registered #registrationStatusDetail",
    );
    const rendered: string = record
      ? original.replace(
          /( · )(PETROL|DIESEL|ELECTRIC|GAS|OTHER|NA)$/,
          (_: string, separator: string, fuel: string): string => separator + translate(fuel),
        )
      : translate(original, Boolean(parent.closest(dynamic)));
    textSources.set(node, { original, rendered });
    if (node.data !== rendered) node.data = rendered;
    return;
  }
  if (!(node instanceof Element) || node.closest(excluded)) return;
  let sources: Map<string, Source> | undefined = attributeSources.get(node);
  if (!sources) {
    sources = new Map();
    attributeSources.set(node, sources);
  }
  for (const name of ["placeholder", "aria-label", "title"]) {
    const value: string | null = node.getAttribute(name);
    if (value === null) continue;
    const previous: Source | undefined = sources.get(name);
    const original: string = previous?.rendered === value ? previous.original : value;
    const rendered: string = translate(original);
    sources.set(name, { original, rendered });
    if (value !== rendered) node.setAttribute(name, rendered);
  }
  for (const child of node.childNodes) render(child);
}
const observer = new MutationObserver((records: MutationRecord[]): void => {
  observer.disconnect();
  for (const record of records) {
    if (record.type === "childList") for (const added of record.addedNodes) render(added);
    else render(record.target);
  }
  observe();
});
function observe(): void {
  observer.observe(document.body, {
    subtree: true,
    childList: true,
    characterData: true,
    attributes: true,
    attributeFilter: ["placeholder", "aria-label", "title"],
  });
}
const nativeConfirm: (message?: string | undefined) => boolean = window.confirm.bind(window);
window.confirm = (message?: string): boolean => nativeConfirm(translate(message ?? "", true));

const themeButton: HTMLButtonElement = element<HTMLButtonElement>("themeToggle");
const systemTheme: MediaQueryList = matchMedia("(prefers-color-scheme: dark)");
let themePreference: string | null = read("seng-theme");
function applyTheme(preference: string | null): void {
  const isDark: boolean = preference === "dark" || (preference !== "light" && systemTheme.matches);
  root.dataset.theme = isDark ? "dark" : "light";
  root.classList.toggle("wa-dark", isDark);
  root.classList.toggle("wa-light", !isDark);
  themeButton.setAttribute("aria-pressed", String(isDark));
  document
    .querySelector('meta[name="theme-color"]')
    ?.setAttribute("content", isDark ? "#101820" : "#142c3e");
}
themeButton.addEventListener("click", (): void => {
  const next: "dark" | "light" = root.dataset.theme === "dark" ? "light" : "dark";
  themePreference = next;
  save("seng-theme", next);
  applyTheme(next);
});
systemTheme.addEventListener("change", (): void => applyTheme(themePreference));
applyTheme(themePreference);

const dialog: HTMLDialogElement = element<HTMLDialogElement>("languageDialog");
const languageButton: HTMLButtonElement = element<HTMLButtonElement>("languageToggle");
const search: HTMLInputElement = element<HTMLInputElement>("languageSearch");
const options: HTMLElement = element("languageOptions");
const fallbackNames: Record<string, string> = {
  ayr: "Aymar aru",
  lus: "Mizo ṭawng",
  mni: "ꯃꯤꯇꯩ ꯂꯣꯟ",
  bho: "भोजपुरी",
  yue: "粵語",
};
function languageName(code: string, displayLocale: string): string {
  try {
    return (
      new Intl.DisplayNames([displayLocale], { type: "language" }).of(code) ??
      fallbackNames[code] ??
      code
    );
  } catch {
    return fallbackNames[code] ?? code;
  }
}
const languages: { code: string; native: string; english: string }[] = localeCodes.map(
  (code: string): { code: string; native: string; english: string } => ({
    code,
    native: fallbackNames[code] ?? languageName(code, code),
    english: languageName(code, "en"),
  }),
);
function normalize(value: string): string {
  return value.normalize("NFD").replace(/\p{M}/gu, "").toLowerCase();
}
function renderLanguages(): void {
  options.replaceChildren();
  const query: string = normalize(search.value.trim());
  for (const language of languages) {
    if (!normalize(`${language.native} ${language.english} ${language.code}`).includes(query))
      continue;
    const button: HTMLButtonElement = document.createElement("button");
    button.type = "button";
    button.className = "language-option";
    button.dataset.locale = language.code;
    button.setAttribute("aria-pressed", String(language.code === locale));
    const name: HTMLSpanElement = document.createElement("span");
    name.textContent = language.native;
    name.lang = language.code;
    name.dir = "auto";
    const code: HTMLElement = document.createElement("small");
    code.textContent = language.code.toUpperCase();
    code.dir = "ltr";
    button.append(name, code);
    button.addEventListener("click", (): void => {
      setLocale(language.code);
      dialog.close();
    });
    options.append(button);
  }
  element("languageEmpty").hidden = options.childElementCount > 0;
}
function setLocale(next: string, persist = true): void {
  const resolved: string | undefined = resolveLocale(next);
  if (!resolved) return;
  locale = resolved;
  if (persist) save("seng-locale", locale);
  root.lang = locale;
  root.dir = rtl.has(locale) ? "rtl" : "ltr";
  element("localeCode").textContent = locale.toUpperCase();
  observer.disconnect();
  render(document.body);
  const title: HTMLTitleElement | null = document.querySelector("title");
  if (title) render(title);
  observe();
  renderLanguages();
}
languageButton.addEventListener("click", (): void => {
  search.value = "";
  renderLanguages();
  dialog.showModal();
  languageButton.setAttribute("aria-expanded", "true");
  search.focus();
});
element("closeLanguage").addEventListener("click", (): void => dialog.close());
dialog.addEventListener("close", (): void => {
  languageButton.setAttribute("aria-expanded", "false");
  languageButton.focus();
});
dialog.addEventListener("click", (event: PointerEvent): void => {
  if (event.target !== dialog) return;
  const box: DOMRect = dialog.getBoundingClientRect();
  if (
    event.clientX < box.left ||
    event.clientX > box.right ||
    event.clientY < box.top ||
    event.clientY > box.bottom
  )
    dialog.close();
});
search.addEventListener("input", renderLanguages);
setLocale(locale, false);
window.addEventListener("storage", (event: StorageEvent): void => {
  if (event.key === "seng-theme" || event.key === null) {
    themePreference = read("seng-theme");
    applyTheme(themePreference);
  }
  if (event.key === "seng-locale" || event.key === null)
    setLocale(read("seng-locale") ?? "en", false);
});
