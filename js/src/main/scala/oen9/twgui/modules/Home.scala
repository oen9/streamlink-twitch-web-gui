package oen9.twgui.modules

import slinky.core.annotations.react
import slinky.core.FunctionalComponent
import slinky.web.html._

@react object Home {
  type Props = Unit
  val component = FunctionalComponent[Props] { _ =>
    div(
      h1("Home"),
      p("This app is similar to streamlink-twitch-gui but works in your web browser."),
      p("Its purpose is to deliver some kind of remote control e.g. using mobile phone with Wi-Fi.")
    )
  }
}
