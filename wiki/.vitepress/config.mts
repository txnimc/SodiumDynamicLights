import { defineConfig, PageData, TransformPageContext } from 'vitepress'
import { applySEO } from './seo';

const req = await fetch(
    'https://raw.githubusercontent.com/nishtahir/language-kotlin/master/dist/Kotlin.JSON-tmLanguage'
)

const kotlin2 = JSON.parse(
    JSON.stringify(await req.json()).replace(/Kotlin/gi, 'kotlin2')
)

// https://vitepress.dev/reference/site-config
export default defineConfig({
  lang: 'en-US',
  title: "TxniTemplate",
  description: "Minecraft Template Mod & Multiversion Library",
  cleanUrls: true,
  appearance: 'dark',

  head: [[
    'link',
    { rel: 'icon', sizes: '32x32', href: '/assets/blahaj-min.png' },
  ]],

  // @ts-ignore
  transformPageData: (pageData: PageData, _ctx: TransformPageContext) => {
    applySEO(pageData);
  },

  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    outline: {
      level: "deep"
    },
    logo: "/assets/blahaj-min.png",
    search: {
      provider: 'local'
    },
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Getting Started', link: '/introduction' }
    ],

    sidebar: [
      {
        text: 'Template Setup',
        items: [
          { text: 'Introduction', link: '/introduction' },
          { text: 'Getting Started', link: '/setup' },
          { text: 'Multiversion Tips', link: '/tips' },
          { text: 'IntelliJ Setup', link: '/intellij' },
          { text: 'Dependencies', link: '/dependencies' },
          { text: 'Flavors', link: '/flavors' },
        ]
      },
      {
        text: 'Library Usage',
        items: [
          { text: 'TxniLib', link: '/lib' }
        ]
      },
      {
        text: 'Other Resources',
        items: [
          { text: 'Helpful Resources', link: '/resources' }
        ]
      }
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/txnimc/TxniTemplate' },
      { icon: 'discord', link: 'https://discord.gg/kS7auUeYmc'}
    ],
    sitemap: {
      hostname: "https://template.txni.dev/"
    },
    markdown: {
      languages: [kotlin2],
      languageAlias: {
        kotlin: 'kotlin2',
        kt: 'kotlin2',
        kts: 'kotlin2'
      }
    }
  }
})
