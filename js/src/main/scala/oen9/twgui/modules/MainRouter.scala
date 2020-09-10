package oen9.twgui.modules

import oen9.twgui.services.AppCircuit
import oen9.twgui.services.ReactDiode
import slinky.core.annotations.react
import slinky.core.FunctionalComponent
import slinky.reactrouter.Route
import slinky.reactrouter.Switch

@react object MainRouter {
  type Props = Unit

  val component = FunctionalComponent[Props] { _ =>
    val routerSwitch = Switch(
      Route(exact = true, path = Loc.home, component = Home.component),
      Route(exact = true, path = Loc.about, component = About.component),
      Route(exact = true, path = Loc.settings, component = Settings.component),
      Route(exact = true, path = Loc.featured, component = Featured.component),
      Route(exact = true, path = Loc.games, component = Games.component),
      Route(exact = true, path = Loc.topStreams, component = TopStreams.component),
      Route(exact = true, path = Loc.followedStreams, component = FollowedStreams.component),
      Route(exact = true, path = Loc.followedChannels, component = FollowedChannels.component)
    )
    ReactDiode.diodeContext.Provider(AppCircuit)(
      Layout(routerSwitch)
    )
  }

  sealed trait MenuItemType
  case class RegularMenuItem(idx: String, label: String, location: String)          extends MenuItemType
  case class MenuItemGroup(idx: String, label: String, items: Seq[RegularMenuItem]) extends MenuItemType

  object Loc {
    val home             = "/"
    val about            = "/about"
    val settings         = "/settings"
    val featured         = "/featured"
    val games            = "/games"
    val topStreams       = "/top-streams"
    val followedStreams  = "/followed-streams"
    val followedChannels = "/followed-channels"
  }

  val menuItems: Seq[MenuItemType] = Seq(
    MenuItemGroup(
      idx = "200",
      label = "Browse",
      items = Seq(
        RegularMenuItem("201", "Featured", Loc.featured),
        RegularMenuItem("202", "Games", Loc.games),
        RegularMenuItem("203", "Top Streams", Loc.topStreams)
      )
    ),
    MenuItemGroup(
      idx = "300",
      label = "Followed",
      items = Seq(
        RegularMenuItem("301", "Streams", Loc.followedStreams),
        RegularMenuItem("302", "Channels", Loc.followedChannels)
      )
    ),
    MenuItemGroup(
      idx = "400",
      label = "Misc",
      items = Seq(
        RegularMenuItem("401", "Home", Loc.home),
        RegularMenuItem("402", "Settings", Loc.settings),
        RegularMenuItem("499", "About", Loc.about)
      )
    )
  )
}
