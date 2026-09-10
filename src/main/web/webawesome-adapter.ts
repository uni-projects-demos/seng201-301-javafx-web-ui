export function enhanceWebAwesome(root: ParentNode = document): HTMLElement[] {
  const upgraded: HTMLElement[] = [];
  function bind(source: HTMLButtonElement, tagName: "wa-button"): void;
  function bind(source: HTMLElement, tagName: "wa-badge" | "wa-divider"): void;
  function bind(source: HTMLElement, tagName: "wa-button" | "wa-badge" | "wa-divider"): void {
    if (source.hasAttribute("data-wa-native-source")) return;
    const component: HTMLElement = document.createElement(tagName);
    if (source.id) {
      component.id = `${source.id}Component`;
      component.dataset.sourceId = source.id;
    }
    const nativeButton: HTMLButtonElement | null =
      tagName === "wa-button" ? (source as HTMLButtonElement) : null;
    const button: WebAwesomeButtonElement | null =
      tagName === "wa-button" ? (component as WebAwesomeButtonElement) : null;
    if (button && nativeButton) {
      button.type = "button";
      component.setAttribute("type", "button");
      component.setAttribute(
        "variant",
        source.classList.contains("button-primary")
          ? "brand"
          : source.classList.contains("button-danger-outline")
            ? "danger"
            : "neutral",
      );
      component.setAttribute(
        "appearance",
        source.classList.contains("button-primary") ? "accent" : "outlined",
      );
      component.addEventListener("click", (event: MouseEvent): void => {
        if (nativeButton.disabled || source.hidden || source.classList.contains("hidden")) {
          event.preventDefault();
          event.stopImmediatePropagation();
          return;
        }
        nativeButton.click();
      });
    }
    if (tagName === "wa-divider") component.setAttribute("aria-hidden", "true");
    function sync(): void {
      component.className = source.className;
      component.textContent = source.textContent;
      component.hidden = source.hidden;
      if (button && nativeButton) button.disabled = nativeButton.disabled;
      for (const name of ["title", "aria-label", "aria-describedby"]) {
        const value: string | null = source.getAttribute(name);
        if (value !== null) component.setAttribute(name, value);
        else component.removeAttribute(name);
      }
    }
    sync();
    const hadFocus: boolean = document.activeElement === source;
    source.after(component);
    source.setAttribute("data-wa-native-source", "");
    new MutationObserver(sync).observe(source, {
      childList: true,
      characterData: true,
      subtree: true,
      attributes: true,
      attributeFilter: ["class", "hidden", "disabled", "title", "aria-label", "aria-describedby"],
    });
    if (hadFocus && button) {
      void button.updateComplete.then((): void => button.focus());
    }
    upgraded.push(component);
  }
  for (const source of root.querySelectorAll<HTMLButtonElement>(
    "button.button, button.small-button",
  )) {
    bind(source, "wa-button");
  }
  for (const source of root.querySelectorAll<HTMLDivElement>("div.divider")) {
    bind(source, "wa-divider");
  }
  const badge: HTMLElement | null = root.querySelector<HTMLElement>("#registrySessionBadge");
  if (badge) bind(badge, "wa-badge");
  return upgraded;
}
