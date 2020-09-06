package oen9.twgui.modules

import slinky.core.annotations.react
import slinky.core.FunctionalComponent
import slinky.reactrouter.Route
import slinky.reactrouter.Switch

@react object MainRouter {
  type Props = Unit

  val component = FunctionalComponent[Props] { _ =>
    val routerSwitch = Switch(
      Route(exact = true, path = Loc.home, component = Home.component),
      Route(exact = true, path = Loc.about, component = About.component)
    )
    Layout(routerSwitch)
  }

  sealed trait MenuItemType
  case class RegularMenuItem(idx: String, label: String, location: String) extends MenuItemType

  object Loc {
    val home  = "/"
    val about = "/about"
  }
  val menuItems: Seq[MenuItemType] = Seq(
    RegularMenuItem("100", "Home", Loc.home),
    RegularMenuItem("1000", "About", Loc.about)
  )
}
