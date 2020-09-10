package oen9.twgui.modules

import slinky.core.annotations.react
import slinky.core.FunctionalComponent
import slinky.web.html._

@react object Featured {
  type Props = Unit
  val component = FunctionalComponent[Props] { _ =>
    div(
      h1("Featured")
    )
  }
}
