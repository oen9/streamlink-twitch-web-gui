package oen9.twgui.modules

import diode.data.PotState.PotEmpty
import diode.data.PotState.PotFailed
import diode.data.PotState.PotPending
import diode.data.PotState.PotReady
import oen9.twgui.components.TokenChecker
import oen9.twgui.services.ajax.TwitchData.UserData
import oen9.twgui.services.AppCircuit
import oen9.twgui.services.CircuitActions.ClearFollowedChannels
import oen9.twgui.services.CircuitActions.TryGetFollowedChannels
import oen9.twgui.services.ReactDiode
import slinky.core.annotations.react
import slinky.core.facade.Hooks._
import slinky.core.facade.ReactElement
import slinky.core.FunctionalComponent
import slinky.web.html._

@react object FollowedChannels {
  type Props = Unit

  val component = FunctionalComponent[Props] { _ =>
    val (me, dispatch)  = ReactDiode.useDiode(AppCircuit.zoom(_.me))
    val (twitchCred, _) = ReactDiode.useDiode(AppCircuit.zoom(_.twitchCred))
    val (followers, _)  = ReactDiode.useDiode(AppCircuit.zoom(_.followers))

    useEffect(() => {
      refreshData()
      () => { dispatch(ClearFollowedChannels) }
    }, Seq(me))

    def refreshData(): Unit =
      me.foreach(m => dispatch(TryGetFollowedChannels(twitchCred.clientId, twitchCred.token, m.id)))

    def prettyChannel(ud: UserData) =
      div(
        key := ud.display_name,
        className := "card card-20 mb-2",
        h5(className := "card-header", ud.display_name),
        div(
          className := "row no-gutters",
          div(className := "col-md-4", img(src := ud.profile_image_url, className := "card-img")),
          div(
            className := "col-md-8",
            div(
              className := "card-body",
              p(className := "card-text", ud.description)
            )
          )
        )
      )

    div(
      h1(
        "Followed Channels",
        button(className := "ml-2 btn btn-primary", "refresh", onClick := (refreshData _))
      ),
      TokenChecker(),
      div(
        followers.state match {
          case PotEmpty =>
            div("nothing here")
          case PotPending =>
            div(
              className := "spinner-border text-primary",
              role := "status",
              span(className := "sr-only", "Loading...")
            )
          case PotFailed =>
            followers.exceptionOption.fold("unknown error")(msg => " error: " + msg.getMessage())
          case PotReady =>
            followers.fold(div("unknown error"): ReactElement) { fs =>
              div(className := "card-deck", fs.data.map(prettyChannel))
            }
          case _ => div()
        }
      )
    )
  }
}
