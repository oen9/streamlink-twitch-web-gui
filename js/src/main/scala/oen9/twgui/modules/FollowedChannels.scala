package oen9.twgui.modules

import oen9.twgui.components.TokenChecker
import slinky.core.annotations.react
import slinky.core.FunctionalComponent
import slinky.web.html._

@react object FollowedChannels {
  type Props = Unit
  val component = FunctionalComponent[Props] { _ =>
    div(
      h1("Followed Channels"),
      TokenChecker()
    )
  }
}
