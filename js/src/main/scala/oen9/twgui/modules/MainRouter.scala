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
  case class RegularMenuItem(idx: String, label: String, location: String)          extends MenuItemType
  case class MenuItemGroup(idx: String, label: String, items: Seq[RegularMenuItem]) extends MenuItemType

  object Loc {
    val home  = "/"
    val about = "/about"
  }
  val menuItems: Seq[MenuItemType] = Seq(
    MenuItemGroup(
      idx = "200",
      label = "Browse",
      items = Seq(
        RegularMenuItem("201", "Featured", Loc.home),
        RegularMenuItem("202", "Games", Loc.home),
        RegularMenuItem("203", "Streams", Loc.home)
      )
    ),
    MenuItemGroup(
      idx = "300",
      label = "Followed",
      items = Seq(
        RegularMenuItem("301", "Streams", Loc.home),
        RegularMenuItem("302", "Channels", Loc.home)
      )
    ),
    MenuItemGroup(
      idx = "400",
      label = "Misc",
      items = Seq(
        RegularMenuItem("401", "Home", Loc.home),
        RegularMenuItem("402", "Settings", Loc.home),
        RegularMenuItem("499", "About", Loc.about)
      )
    ),
  )
}
