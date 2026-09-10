import { enhanceWebAwesome } from "./webawesome-adapter.ts";

const ready: Promise<void> =
  document.readyState === "loading"
    ? new Promise<void>((resolve: (value: void | PromiseLike<void>) => void): void => {
        document.addEventListener("DOMContentLoaded", (): void => resolve(), { once: true });
      })
    : Promise.resolve();

void Promise.all([
  ready,
  import("@awesome.me/webawesome/dist/components/button/button.js"),
  import("@awesome.me/webawesome/dist/components/badge/badge.js"),
  import("@awesome.me/webawesome/dist/components/divider/divider.js"),
])
  .then(async (): Promise<void> => {
    await Promise.all(
      ["wa-button", "wa-badge", "wa-divider"].map(
        (name: string): Promise<CustomElementConstructor> => customElements.whenDefined(name),
      ),
    );
    enhanceWebAwesome();
    document.documentElement.dataset.webawesome = "ready";
  })
  .catch((error: unknown): void => {
    document.documentElement.dataset.webawesome = "fallback";
    console.warn("Web Awesome could not load; native controls remain active.", error);
  });
