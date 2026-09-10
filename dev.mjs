import { watch } from "node:fs";
import { context } from "esbuild";
import { buildStyles, compileJava, copyHtml, copyRuntime, runBuild, runChecks } from "./build.mjs";

await runBuild({ cleanJava: false });
const ctx = await context({
  entryPoints: ["src/main/web/bootstrap.ts"],
  bundle: true,
  format: "esm",
  target: "es2022",
  outfile: "dist/app.js",
});
await ctx.rebuild();
const { port } = await ctx.serve({ servedir: "dist", host: "127.0.0.1", port: 8080 });
console.log(`Server running: http://localhost:${port}\nRefresh after a successful rebuild.`);

let javaPending = false;
let javaDirty = false;
let webPending = false;
let running = false;
let timer;
async function rebuild() {
  if (running) return;
  running = true;
  try {
    while (javaPending || webPending) {
      const javaChanged = javaPending || javaDirty;
      javaPending = false;
      webPending = false;
      try {
        runChecks();
        if (javaChanged) {
          await compileJava();
          await copyRuntime();
          javaDirty = false;
        }
        await buildStyles();
        await copyHtml();
        await ctx.rebuild();
        console.log("Rebuilt successfully. Refresh your browser.");
      } catch (error) {
        if (javaChanged) javaDirty = true;
        console.error("Rebuild failed; fix the error and save again.", error);
      }
    }
  } finally {
    running = false;
  }
}
function schedule(javaChanged) {
  javaPending ||= javaChanged;
  webPending = true;
  clearTimeout(timer);
  timer = setTimeout(rebuild, 150);
}
const watchers = [
  watch("src/main/web", { recursive: true }, () => schedule(false)),
  watch("src/main/java", { recursive: true }, (_, file) => {
    if (file?.endsWith(".java")) schedule(true);
  }),
];
async function stop() {
  clearTimeout(timer);
  for (const watcher of watchers) {
    watcher.close();
  }
  await ctx.dispose();
  process.exit(0);
}
process.on("SIGINT", stop);
process.on("SIGTERM", stop);
