export function element(id: string | null): HTMLElement {
  const node: HTMLElement | null = id ? document.getElementById(id) : null;
  if (!node) throw new Error(`Missing UI element: ${id}`);
  return node;
}

export function itemAt<T>(items: readonly T[], index: number): T {
  const item: T | undefined = items[index];
  if (item === undefined) throw new RangeError(`Invalid UI index: ${index}`);
  return item;
}
