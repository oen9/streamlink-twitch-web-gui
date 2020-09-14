package oen9.twgui.modules

import diode.data.PotState.PotEmpty
import diode.data.PotState.PotFailed
import diode.data.PotState.PotPending
import diode.data.PotState.PotReady
import oen9.twgui.services.ajax.TwitchData.StreamData
import oen9.twgui.services.AppCircuit
import oen9.twgui.services.CircuitActions.ClearGames
import oen9.twgui.services.CircuitActions.ClearStreams
import oen9.twgui.services.CircuitActions.TryGetStreams
import oen9.twgui.services.ReactDiode
import slinky.core.annotations.react
import slinky.core.facade.Hooks._
import slinky.core.facade.ReactElement
import slinky.core.FunctionalComponent
import slinky.web.html._

@react object TopStreams {
  type Props = Unit
  val component = FunctionalComponent[Props] { _ =>
    val (streams, dispatch) = ReactDiode.useDiode(AppCircuit.zoom(_.streams))
    val (games, _)          = ReactDiode.useDiode(AppCircuit.zoom(_.games))
    val (twitchCred, _)     = ReactDiode.useDiode(AppCircuit.zoom(_.twitchCred))

    useEffect(() => {
      refreshData()
      () => { dispatch(ClearStreams); dispatch(ClearGames) }
    }, Seq())

    def refreshData(): Unit = dispatch(TryGetStreams(twitchCred.clientId, twitchCred.token))

    def prettyStream(sd: StreamData) =
      div(
        key := sd.user_name,
        className := "card card-14 mb-2",
        img(src := sd.thumbnail_url.replace("{width}", "440").replace("{height}", "248"), className := "card-img-top"),
        h5(className := "card-header", sd.user_name),
        div(
          className := "card-body",
          h5(className := "card-title", gameIdToName(sd.game_id)),
          p(className := "card-text", small(sd.title)),
        ),
        div(className := "card-footer", a(href := "", className := "btn btn-primary", "play"))
      )

    def gameIdToName(id: String): String = games.state match {
      case PotPending =>
        "loading..."
      case PotFailed =>
        streams.exceptionOption.fold("unknown error")(msg => " error: " + msg.getMessage())
      case PotReady =>
        games.fold("unknown game")(_.data.find(_.id == id).map(_.name).getOrElse("unknownGame"))
      case _ => "unknown"
    }

    div(
      h1(
        "Top Streams",
        button(className := "ml-2 btn btn-primary", "refresh", onClick := (refreshData _))
      ),
      div(
        streams.state match {
          case PotEmpty =>
            div("nothing here")
          case PotPending =>
            div(
              className := "spinner-border text-primary",
              role := "status",
              span(className := "sr-only", "Loading...")
            )
          case PotFailed =>
            streams.exceptionOption.fold("unknown error")(msg => " error: " + msg.getMessage())
          case PotReady =>
            streams.fold(div("unknown error"): ReactElement) { sdata =>
              div(className := "card-deck", sdata.data.map(prettyStream))
            }
          case _ => div()
        }
      )
    )
  }
}
