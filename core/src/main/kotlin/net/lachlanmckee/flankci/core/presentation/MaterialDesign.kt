package net.lachlanmckee.flankci.core.presentation

import kotlinx.html.*

fun HTML.materialHeader(contentFunc: HEAD.() -> Unit = {}) {
  head {
    link(rel = "stylesheet", href = "https://fonts.googleapis.com/icon?family=Material+Icons")
    link(rel = "stylesheet", href = "https://code.getmdl.io/1.3.0/material.teal-indigo.min.css")
    script {
      src = "https://code.getmdl.io/1.3.0/material.min.js"
    }
    link(rel = "stylesheet", href = "/static/styles.css", type = "text/css")

    contentFunc(this)
  }
}

fun HTML.materialBody(
  title: String,
  linksFunc: (NAV.(MaterialLinkMode) -> Unit)? = {},
  contentFunc: HtmlBlockTag.() -> Unit
) {
  body {
    div {
      classes = setOf("mdl-layout", "mdl-js-layout", "mdl-layout--fixed-header")
      header("mdl-layout__header") {
        div("mdl-layout__header-row") {
          span("mdl-layout-title") { +title }

          if (linksFunc != null) {
            nav {
              classes = setOf("mdl-navigation")
              linksFunc(MaterialLinkMode.TOOLBAR)
            }
          }
        }
      }
      if (linksFunc != null) {
        div("mdl-layout__drawer") {
          span("mdl-layout-title") { +title }

          nav {
            classes = setOf("mdl-navigation")
            linksFunc(MaterialLinkMode.DRAWER)
          }
        }
      }
      main("mdl-layout__content") {
        div("page-content") {
          contentFunc()
        }
      }
    }
  }
}

fun NAV.materialStandardLink(text: String, href: String, icon: String, newWindow: Boolean) {
  a(href, if (newWindow) "_blank" else "_self") {
    classes = setOf("mdl-navigation__link", "mdl-navigation__link--icon")
    i {
      classes = setOf("material-icons")
      text(icon)
    }
    span {
      text(text)
    }
  }
}

fun NAV.materialJavascriptLink(text: String, onClick: String, icon: String) {
  a("#") {
    this.onClick = onClick
    classes = setOf("mdl-navigation__link", "mdl-navigation__link--icon")
    i {
      classes = setOf("material-icons")
      text(icon)
    }
    span {
      text(text)
    }
  }
}

enum class MaterialLinkMode {
  TOOLBAR, DRAWER
}
