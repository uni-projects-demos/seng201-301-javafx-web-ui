import { element, itemAt } from "./dom.ts";

((): void => {
  const title: HTMLElement = element("pageTitle");
  function updateTitle(): void {
    if (title.textContent === "Airport parking") {
      title.textContent = "Christchurch International Airport Parking";
    }
  }
  new MutationObserver(updateTitle).observe(title, {
    childList: true,
    characterData: true,
    subtree: true,
  });
  updateTitle();
  const tabs: HTMLButtonElement[] = [
    ...document.querySelectorAll<HTMLButtonElement>('.auth-tabs [role="tab"]'),
  ];
  const message: HTMLElement = element("registryAuthMessage");
  function activate(tab: HTMLButtonElement, focus: boolean = false): void {
    for (const item of tabs) {
      const selected: boolean = item === tab;
      item.setAttribute("aria-selected", String(selected));
      item.tabIndex = selected ? 0 : -1;
      item.classList.toggle("active", selected);
      element(item.getAttribute("aria-controls")).hidden = !selected;
    }
    message.textContent = "";
    message.className = "session-message compact-message";
    if (focus) tab.focus();
  }
  tabs.forEach((tab: HTMLButtonElement, index: number): void => {
    tab.addEventListener("click", (): void => activate(tab));
    tab.addEventListener("keydown", (event: KeyboardEvent): void => {
      let next: number | undefined;
      if (event.key === "ArrowRight") next = (index + 1) % tabs.length;
      if (event.key === "ArrowLeft") next = (index + tabs.length - 1) % tabs.length;
      if (event.key === "Home") next = 0;
      if (event.key === "End") next = tabs.length - 1;
      if (next !== undefined) {
        event.preventDefault();
        activate(itemAt(tabs, next), true);
      }
    });
  });
  element("logoutOwner").addEventListener("click", (): void => activate(itemAt(tabs, 0)));
  const duration: HTMLElement = element("quoteDuration");
  function updateDuration(): void {
    const text: string = duration.textContent?.trim() ?? "";
    duration.hidden =
      !text ||
      [
        "Use the original pricing rules.",
        "Using current published drive-up rates.",
        "Use legacy rates.",
        "Use current rates.",
      ].includes(text);
  }
  new MutationObserver(updateDuration).observe(duration, {
    childList: true,
    characterData: true,
    subtree: true,
  });
  updateDuration();

  const compact: MediaQueryList = window.matchMedia("(max-width: 1050px)");
  type SelectPanel = (index: number, focus?: boolean) => void;
  const workspaces: Map<HTMLElement, SelectPanel> = new Map<HTMLElement, SelectPanel>();
  document
    .querySelectorAll<HTMLElement>(".panel-workspace")
    .forEach((workspace: HTMLElement): void => {
      const panelTabs: HTMLButtonElement[] = [
        ...workspace.querySelectorAll<HTMLButtonElement>('.panel-tabs [role="tab"]'),
      ];
      const panels: HTMLElement[] = panelTabs.map(
        (tab: HTMLButtonElement): HTMLElement => element(tab.getAttribute("aria-controls")),
      );
      let selected: number = 0;
      function render(): void {
        panels.forEach((panel: HTMLElement, index: number): void => {
          panel.hidden = compact.matches && index !== selected;
          if (compact.matches) {
            panel.setAttribute("role", "tabpanel");
            panel.setAttribute("aria-labelledby", itemAt(panelTabs, index).id);
          } else {
            panel.removeAttribute("role");
            panel.removeAttribute("aria-labelledby");
          }
          itemAt(panelTabs, index).setAttribute("aria-selected", String(index === selected));
          itemAt(panelTabs, index).tabIndex = index === selected ? 0 : -1;
        });
        workspace.dataset.panelsReady = "true";
      }
      function select(index: number, focus: boolean = false): void {
        selected = index;
        render();
        if (focus && compact.matches) itemAt(panelTabs, index).focus();
      }
      panelTabs.forEach((tab: HTMLButtonElement, index: number): void => {
        tab.addEventListener("click", (): void => select(index));
        tab.addEventListener("keydown", (event: KeyboardEvent): void => {
          const keys: Partial<Record<string, number>> = {
            ArrowRight: (index + 1) % panelTabs.length,
            ArrowLeft: (index + panelTabs.length - 1) % panelTabs.length,
            Home: 0,
            End: panelTabs.length - 1,
          };
          const next: number | undefined = keys[event.key];
          if (next !== undefined) {
            event.preventDefault();
            select(next, true);
          }
        });
      });
      compact.addEventListener("change", (): void => {
        const focusedPanel: number = panels.findIndex((panel: HTMLElement): boolean =>
          panel.contains(document.activeElement),
        );
        if (focusedPanel >= 0) selected = focusedPanel;
        const focusedTab: boolean = panelTabs.some(
          (tab: HTMLButtonElement): boolean => tab === document.activeElement,
        );
        render();
        if (!compact.matches && focusedTab) {
          itemAt(panels, selected)
            .querySelector<HTMLElement>(
              "input, select, wa-button, button:not([data-wa-native-source])",
            )
            ?.focus();
        }
      });
      workspaces.set(workspace, select);
      render();
    });
  function openEditor(): void {
    if (compact.matches) workspaces.get(element("registrationDashboard"))?.(2, true);
  }
  element("newVehicle").addEventListener("click", openEditor);
  element("vehicleList").addEventListener("click", (event: MouseEvent): void => {
    if (event.target instanceof Element && event.target.closest("[data-plate]")) openEditor();
  });
  for (const sourceId of ["message", "registryMessage"]) {
    const source: HTMLElement = element(sourceId);
    const copies: HTMLElement[] = [
      ...document.querySelectorAll<HTMLElement>(`[data-feedback-source="${sourceId}"]`),
    ];
    function syncFeedback(): void {
      for (const copy of copies) {
        copy.textContent = source.textContent;
        copy.className = `compact-feedback ${source.className}`;
      }
    }
    new MutationObserver(syncFeedback).observe(source, {
      childList: true,
      characterData: true,
      subtree: true,
      attributes: true,
      attributeFilter: ["class"],
    });
    syncFeedback();
  }
})();

const historyState: HTMLElement = element("vehiclehistTab");
const historyTab: HTMLElement = element("vehicleHistTab");
const detailsTab: HTMLElement = element("vehicleDetailsTab");
function syncHistoryTab(): void {
  const selected: boolean = historyState.classList.contains("active");
  historyTab.classList.toggle("active", selected);
  for (const [tab, active] of [
    [historyTab, selected],
    [detailsTab, !selected],
  ] as const) {
    tab.setAttribute("aria-selected", String(active));
    tab.tabIndex = active ? 0 : -1;
  }
}
new MutationObserver(syncHistoryTab).observe(historyState, {
  attributes: true,
  attributeFilter: ["class"],
});
syncHistoryTab();
for (const tab of [detailsTab, historyTab]) {
  tab.addEventListener("keydown", (event: KeyboardEvent): void => {
    if (!["ArrowLeft", "ArrowRight", "Home", "End"].includes(event.key)) return;
    event.preventDefault();
    const target: HTMLElement =
      event.key === "Home"
        ? detailsTab
        : event.key === "End"
          ? historyTab
          : tab === detailsTab
            ? historyTab
            : detailsTab;
    target.click();
    target.focus();
  });
}
