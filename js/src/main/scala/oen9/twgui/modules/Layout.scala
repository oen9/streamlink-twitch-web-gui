package oen9.twgui.modules

import diode.data.PotState.PotEmpty
import diode.data.PotState.PotReady
import oen9.twgui.bridges.reactrouter.NavLink
import oen9.twgui.bridges.reactrouter.ReactRouterDOM
import oen9.twgui.modules.MainRouter.Loc
import oen9.twgui.modules.MainRouter.MenuItemGroup
import oen9.twgui.modules.MainRouter.RegularMenuItem
import oen9.twgui.services.AppCircuit
import oen9.twgui.services.CircuitActions.GetDefaultTwitchCred
import oen9.twgui.services.CircuitActions.TryGetMe
import oen9.twgui.services.ReactDiode
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import slinky.core.facade.Hooks._
import slinky.core.facade.ReactElement
import slinky.core.FunctionalComponent
import slinky.reactrouter.Link
import slinky.web.html._

@react object Layout {
  case class Props(content: ReactElement)

  def createRegularMenuItem(idx: String, label: String, location: String): ReactElement =
    li(key := idx, className := "nav-item", NavLink(exact = true, to = location)(className := "nav-link", label))

  def createMenuItemGroup(currentPath: String, idx: String, label: String, items: Seq[RegularMenuItem]): ReactElement =
    Fragment(
      key := idx,
      li(h4(label)),
      items.map(item => createRegularMenuItem(item.idx, item.label, item.location))
    )

  def nav(props: Props, currentPath: String, tokenDisplayName: String) =
    div(
      className := "navbar navbar-expand-md navbar-dark bg-secondary",
      Link(to = Loc.home)(
        className := "navbar-brand",
        img(className := "logo", src := "front-res/img/Twitch_Logo_Purple.png", alt := "twitch")
      ),
      button(
        className := "navbar-toggler",
        `type` := "button",
        data - "toggle" := "collapse",
        data - "target" := "#navbarNav",
        aria - "controls" := "navbarNav",
        aria - "expanded" := "false",
        aria - "label" := "Toggle navigation",
        span(className := "navbar-toggler-icon")
      ),
      div(
        className := "collapse navbar-collapse",
        id := "navbarNav",
        ul(
          className := "navbar-nav mr-auto"
        ),
        span(className := "mr-2", tokenDisplayName),
        NavLink(exact = true, to = Loc.settings)(
          className := "btn btn-primary d-lg-inline-block",
          i(className := "fas fa-cog")
        )
      )
    )

  def sidebar(props: Props, currentPath: String) =
    div(
      className := "navbar navbar-expand-md navbar-light bg-light",
      button(
        className := "navbar-toggler",
        `type` := "button",
        data - "toggle" := "collapse",
        data - "target" := "#sidebarNav",
        aria - "controls" := "sidebarNav",
        aria - "expanded" := "false",
        aria - "label" := "Toggle navigation",
        span(className := "navbar-toggler-icon")
      ),
      div(
        className := "collapse navbar-collapse",
        id := "sidebarNav",
        ul(
          className := "navbar-nav mr-auto flex-column",
          MainRouter.menuItems.map(_ match {
            case RegularMenuItem(idx, label, location) => createRegularMenuItem(idx, label, location)
            case MenuItemGroup(idx, label, items)      => createMenuItemGroup(currentPath, idx, label, items)
          })
        )
      )
    )

  def contentBody(props: Props) = props.content

  def footer(props: Props) =
    div(className := "footer bg-secondary text-white d-flex justify-content-center mt-auto py-3", "© 2020 oen")

  val component = FunctionalComponent[Props] { props =>
    val location        = ReactRouterDOM.useLocation()
    val (me, dispatch)  = ReactDiode.useDiode(AppCircuit.zoom(_.me))
    val (twitchCred, _) = ReactDiode.useDiode(AppCircuit.zoom(_.twitchCred))

    useEffect(
      () => {
        me.state match {
          case PotEmpty => dispatch(TryGetMe(twitchCred.clientId, twitchCred.token))
          case _        => ()
        }
        if (twitchCred.clientId.isEmpty() && twitchCred.token.isEmpty()) dispatch(GetDefaultTwitchCred)
      },
      Seq()
    )

    val tokenDisplayName = me.state match {
      case PotReady => me.fold("not logged in")(_.display_name)
      case _        => "not logged in"
    }

    Fragment(
      nav(props, location.pathname, tokenDisplayName),
      div(
        className := "container-fluid mt-2",
        div(
          className := "row flex-xl-nowrap",
          div(className := "col-md-3 col-xl-2 bd-sidebar mb-2", sidebar(props, location.pathname)),
          main(
            className := "col-md-9 col-xl-10 py-md-3 pl-md-5 mb-2 bd-content",
            role := "main",
            contentBody(props)
          )
        )
      ),
      footer(props)
    )
  }
}
