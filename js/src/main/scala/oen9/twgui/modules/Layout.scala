package oen9.twgui.modules

import oen9.twgui.bridges.reactrouter.NavLink
import oen9.twgui.bridges.reactrouter.ReactRouterDOM
import oen9.twgui.modules.MainRouter.Loc
import oen9.twgui.modules.MainRouter.RegularMenuItem
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import slinky.core.facade.ReactElement
import slinky.core.FunctionalComponent
import slinky.reactrouter.Link
import slinky.web.html._

@react object Layout {
  case class Props(content: ReactElement)

  def createRegularMenuItem(idx: String, label: String, location: String) =
    li(key := idx, className := "nav-item", NavLink(exact = true, to = location)(className := "nav-link", label))

  def nav(props: Props, currentPath: String) =
    div(
      className := "navbar navbar-expand-md navbar-dark bg-primary",
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
          className := "navbar-nav mr-auto",
          MainRouter.menuItems.map(_ match {
            case RegularMenuItem(idx, label, location) => createRegularMenuItem(idx, label, location)
          })
        )
      )
    )

  def contentBody(props: Props) = props.content

  def footer(props: Props) =
    div(className := "footer bg-primary text-white d-flex justify-content-center mt-auto py-3", "© 2020 oen")

  val component = FunctionalComponent[Props] { props =>
    val location = ReactRouterDOM.useLocation()

    Fragment(
      nav(props, location.pathname),
      div(className := "container", div(className := "main-content mt-5", role := "main", contentBody(props))),
      footer(props)
    )
  }
}
