package net.lachlanmckee.flankci.core.presentation

import kotlinx.html.*

fun HtmlBlockTag.button(label: String, gray: Boolean, func: A.() -> Unit) {
  a {
    classes = mutableSetOf("mdl-button mdl-button--colored", "mdl-js-button", "mdl-js-ripple-effect")
      .apply {
        if (gray) {
          add("gray-button")
        }
      }
    func(this)
    text(label)
  }
  text(" ")
}

fun HtmlBlockTag.linkButton(label: String, url: String, gray: Boolean = true) {
  button(label, gray) {
    href = url
    target = "_blank"
  }
}

fun HtmlBlockTag.jsButton(label: String, onClick: String, gray: Boolean = true) {
  button(label, gray) {
    this.onClick = onClick
  }
}
