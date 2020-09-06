package oen9.twgui.modules

import slinky.core.annotations.react
import slinky.core.facade.Fragment
import slinky.core.facade.ReactElement
import slinky.core.FunctionalComponent
import slinky.web.html._

@react object Layout {
  case class Props(content: ReactElement)

  val component = FunctionalComponent[Props] { props =>
    Fragment(
      div(className := "container", props.content)
    )
  }
}
