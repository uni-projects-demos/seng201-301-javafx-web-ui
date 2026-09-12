import { readdirSync, readFileSync } from "node:fs";

const web = new URL("../src/main/web/i18n/", import.meta.url);
const localesDir = new URL("locales/", web);

const read = (name) => JSON.parse(readFileSync(new URL(name, web), "utf8"));

export function checkLocales() {
  const codes = readdirSync(localesDir)
    .filter((name) => name.endsWith(".json"))
    .map((name) => name.replace(/\.json$/, ""));

  if (codes.length !== 124 || new Set(codes).size !== codes.length) {
    throw new Error("Expected exactly 124 unique locales");
  }

  const en = read("locales/en.json");
  const keys = Object.keys(en);

  const placeholders = (text) => JSON.stringify((text.match(/\{\d+}/g) ?? []).sort());

  const registry = readFileSync(new URL("locale-catalogs.ts", web), "utf8");

  for (const code of codes) {
    if (!registry.includes(`./locales/${code}.json`)) {
      throw new Error(`${code}: not imported into the browser bundle`);
    }

    const catalog = read(`locales/${code}.json`);

    for (const key of keys) {
      if (typeof catalog[key] !== "string" || !catalog[key].trim()) {
        throw new Error(`${code}: missing ${key}`);
      }

      if (placeholders(key) !== placeholders(catalog[key])) {
        throw new Error(`${code}: invalid placeholders in ${key}`);
      }
    }
  }

  console.log(
    `Validated ${codes.length} locales × ${keys.length} source messages and interpolation placeholders.`,
  );
}
