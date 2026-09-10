import { spawn, spawnSync } from "node:child_process";
import { readFileSync } from "node:fs";
import { copyFile, mkdir, readdir, readFile, rm, stat, writeFile } from "node:fs/promises";
import { createRequire } from "node:module";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import { build } from "esbuild";
import { compile } from "tailwindcss";

const root = dirname(fileURLToPath(import.meta.url));
process.chdir(root);
const require = createRequire(import.meta.url);

function runMaven(args) {
  return new Promise((resolve, reject) => {
    const windows = process.platform === "win32";
    const child = spawn(
      windows ? "cmd.exe" : "mvn",
      windows ? ["/d", "/s", "/c", `mvn.cmd ${args.join(" ")}`] : args,
      { cwd: root, stdio: "inherit" },
    );
    child.on("error", (error) =>
      reject(
        new Error(
          `Cannot start Maven. Install it and add its bin directory to PATH. ${error.message}`,
        ),
      ),
    );
    child.on("exit", (code) =>
      code === 0 ? resolve() : reject(new Error(`Maven failed (${code}); check the output above.`)),
    );
  });
}

export function checkDom() {
  const read = (path) => readFileSync(fileURLToPath(new URL(path, import.meta.url)), "utf8");
  const html = read("./src/main/web/index.html");
  const java = read("./src/main/java/airport/web/AirportParkingWebApp.java");
  const ids = [...html.matchAll(/\bid="([^"]+)"/g)].map((match) => match[1]);
  const unique = new Set(ids);
  const problems = [];
  if (unique.size !== ids.length) problems.push("Duplicate HTML IDs");
  const references = [
    ...html.matchAll(/\b(?:for|aria-controls|aria-labelledby|aria-describedby)="([^"]+)"/g),
  ].flatMap((match) => match[1].split(/\s+/));
  const methods =
    "onClick|onEnter|onInput|onChange|onDataClick|text|setVisible|setActiveButton|setValue|value|setButtonText|setClassName|setHtml|setDisabled|setMessageBox";
  references.push(
    ...[...java.matchAll(new RegExp(`(?:${methods})\\("([^"]+)"`, "g"))].map((match) => match[1]),
  );
  for (const id of new Set(references))
    if (!unique.has(id)) problems.push(`Missing HTML element: ${id}`);
  if (problems.length) {
    throw new Error(problems.join("\n"));
  }
  console.log("HTML IDs, label/ARIA references and Java control bindings agree.");
}

export function runChecks() {
  checkDom();
  const result = spawnSync(process.execPath, ["node_modules/typescript/bin/tsc", "--noEmit"], {
    cwd: root,
    stdio: "inherit",
  });
  if (result.error) throw result.error;
  if (result.status !== 0) throw new Error("TypeScript validation failed.");
}

export async function compileJava(clean = false) {
  // Never accept a leftover TeaVM bundle as evidence of a successful build.
  await rm("target/javascript", { recursive: true, force: true });
  await runMaven(clean ? ["-B", "clean", "process-classes"] : ["-B", "process-classes"]);
  await requireRuntime();
}

async function requireRuntime() {
  const path = "target/javascript/airport-parking.js";
  if (!(await stat(path)).size) throw new Error(`TeaVM generated an empty ${path}`);
}

export async function copyRuntime() {
  await requireRuntime();
  await copyFile("target/javascript/airport-parking.js", "dist/airport-parking.js");
}

export async function copyHtml() {
  await copyFile("src/main/web/index.html", "dist/index.html");
}

export async function buildStyles() {
  const html = await readFile("src/main/web/index.html", "utf8");
  const candidates = [
    ...new Set(
      [...html.matchAll(/class="([^"]*)"/g)]
        .flatMap((match) => match[1].split(/\s+/))
        .filter((name) => name.startsWith("tw:")),
    ),
  ];

  const input = resolve("src/main/web/style.css");
  const compiler = await compile(await readFile(input, "utf8"), {
    base: dirname(input),
    from: input,
    async loadStylesheet(id, base) {
      const path = id.startsWith(".") ? resolve(base, id) : require.resolve(id);
      return { path, base: dirname(path), content: await readFile(path, "utf8") };
    },
  });

  await build({
    stdin: {
      contents:
        '@import "@awesome.me/webawesome/dist/styles/themes/default.css";\n' +
        compiler.build(candidates),
      loader: "css",
      resolveDir: root,
    },
    bundle: true,
    minify: true,
    outfile: "dist/style.css",
  });
}

export async function runBuild({ cleanJava = true } = {}) {
  runChecks();

  console.log("==> Compiling Java / TeaVM...");
  await compileJava(cleanJava);

  await rm("dist", { recursive: true, force: true });
  await mkdir("dist", { recursive: true });

  await buildStyles();

  await build({
    entryPoints: ["src/main/web/bootstrap.ts"],
    bundle: true,
    format: "esm",
    target: "es2022",
    minify: true,
    legalComments: "inline",
    outfile: "dist/app.js",
  });

  await copyHtml();
  await copyRuntime();

  const notices = [];
  async function licenses(dir) {
    for (const entry of await readdir(dir, { withFileTypes: true })) {
      const path = join(dir, entry.name);
      if (entry.isDirectory()) await licenses(path);
      else if (/^(license|copying|thirdpartynotice)([.-]|$)/i.test(entry.name)) {
        notices.push(`${path}\n${await readFile(path, "utf8")}`);
      }
    }
  }
  await licenses("node_modules");
  await writeFile("dist/THIRD_PARTY_LICENSES.txt", notices.join("\n\n---\n\n"));

  console.log("==> Built dist/ successfully.");
}

if (process.argv[1] && resolve(process.argv[1]) === fileURLToPath(import.meta.url)) {
  const command = process.argv[2];
  const task =
    command === "--check"
      ? async () => runChecks()
      : command === "--clean"
        ? async () => {
            await rm("dist", { recursive: true, force: true });
            await runMaven(["clean"]);
          }
        : runBuild;
  task().catch((error) => {
    console.error(error);
    process.exit(1);
  });
}
