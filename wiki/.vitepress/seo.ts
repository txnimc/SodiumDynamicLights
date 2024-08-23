import { PageData } from "vitepress";

function addTag(pageData: PageData, name: string, content: string) {
  pageData.frontmatter.head ??= [];
  pageData.frontmatter.head.push([
    "meta",
    {
      name,
      content,
    },
  ]);
}

export function applySEO(pageData: PageData) {
  addTag(
    pageData,
    "og:title",
    pageData.title === "TxniTemplate"
      ? `TxniTemplate`
      : `${pageData.title} | TxniTemplate`
  );

  addTag(pageData, "og:type", "website");
  addTag(pageData, "og:url", `https://template.txni.dev/${pageData.relativePath}`);
  addTag(pageData, "og:description", pageData.description);
  addTag(pageData, "og:image", "/assets/blahaj-min.png");
  addTag(pageData, "og:image:width", "128");
  addTag(pageData, "og:image:height", "128");

  addTag(pageData, "twitter:card", "summary");

  addTag(pageData, "theme-color", "#FF6F95");

  // Dont index the page if it's a versioned page.
  const path = pageData.filePath;
  if (path.includes("versions")) {
    addTag(pageData, "robots", "noindex");
  }
}