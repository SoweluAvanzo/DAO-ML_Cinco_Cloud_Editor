const env = require("./env.default");
const { description } = require('../../package')

module.exports = {
  /**
   * Ref：https://v1.vuepress.vuejs.org/config/#title
   */
  title: 'CincoCloud Wiki',
  /**
   * Ref：https://v1.vuepress.vuejs.org/config/#description
   */
  description: description,

  base: env.BASE,

  /**
   * Extra tags to be injected to the page HTML `<head>`
   *
   * ref：https://v1.vuepress.vuejs.org/config/#head
   */
  head: [
    ['meta', { name: 'theme-color', content: '#3e89af' }],
    ['meta', { name: 'apple-mobile-web-app-capable', content: 'yes' }],
    ['meta', { name: 'apple-mobile-web-app-status-bar-style', content: 'black' }]
  ],

  markdown: {
    toc:  {
      includeLevel: [2, 3]
    },
    lineNumbers: true
  },


  /**
   * Theme configuration, here is the default theme configuration for VuePress.
   *
   * ref：https://v1.vuepress.vuejs.org/theme/default-theme-config.html
   */
  themeConfig: {
    repo: '',
    editLinks: false,
    docsDir: '',
    editLinkText: '',
    lastUpdated: true,
    nav: [
      {
        text: 'Guide',
        link: '/content/introduction/',
      },
      {
        text: 'CincoCloud@GitLab',
        link: 'https://gitlab.com/scce/cinco-cloud'
      }
    ],
    sidebar: [
      ['content/introduction/', 'Introduction'],
      {
        title: 'User Guide',
        sidebarDepth: 2,
        children: [
          {
            title: 'Building Cinco products',
            path: '/content/user-guide/building-cinco-products/'
          }
        ]
      },
      {
        title: 'Developer Guide',
        sidebarDepth: 2,
        children: [
          {
            title: 'Overview',
            path: '/content/developer-guide/overview/'
          },
          {
            title: 'Installation',
            path: '/content/developer-guide/installation/'
          },
          {
            title: 'Developing on CincoCloud',
            path: '/content/developer-guide/developing/'
          },
          {
            title: 'Developing for the Theia Editor',
            path: '/content/developer-guide/developing-theia/'
          },
          {
            title: 'Developing for the Language-Server',
            path: '/content/developer-guide/developing-ls/'
          }
        ]
      },
      ['content/operator-guide/', 'Operator Guide']
    ]
  },

  /**
   * Apply plugins，ref：https://v1.vuepress.vuejs.org/zh/plugin/
   */
  plugins: [
    '@vuepress/plugin-back-to-top',
    '@vuepress/plugin-medium-zoom',
  ]
}
