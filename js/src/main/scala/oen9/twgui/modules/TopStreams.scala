package oen9.twgui.modules

import slinky.core.annotations.react
import slinky.core.FunctionalComponent
import slinky.web.html._

@react object TopStreams {
  type Props = Unit
  val component = FunctionalComponent[Props] { _ =>
    div(
      "Top Streams"
    )
  }
}