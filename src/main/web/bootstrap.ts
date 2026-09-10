import "./ui.ts";
import "./webawesome-init.ts";

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", (): void => main(), { once: true });
} else {
  main();
}
