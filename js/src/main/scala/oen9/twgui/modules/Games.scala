package oen9.twgui.modules

import cats.implicits._
import diode.data.PotState.PotEmpty
import diode.data.PotState.PotFailed
import diode.data.PotState.PotPending
import diode.data.PotState.PotReady
import oen9.twgui.components.FetchMoreButton
import oen9.twgui.services.ajax.TwitchData.GameData
import oen9.twgui.services.AppCircuit
import oen9.twgui.services.CircuitActions.ClearGames
import oen9.twgui.services.CircuitActions.TryGetGamesTop
import oen9.twgui.services.CircuitActions.TryGetNextGamesTop
import oen9.twgui.services.ReactDiode
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import slinky.core.facade.Hooks._
import slinky.core.facade.ReactElement
import slinky.core.FunctionalComponent
import slinky.web.html._

@react object Games {
  type Props = Unit

  val component = FunctionalComponent[Props] { _ =>
    val (games, dispatch) = ReactDiode.useDiode(AppCircuit.zoom(_.games))
    val (nextGames, _)    = ReactDiode.useDiode(AppCircuit.zoom(_.nextGames))
    val (twitchCred, _)   = ReactDiode.useDiode(AppCircuit.zoom(_.twitchCred))

    useEffect(() => {
      refreshData()
      () => dispatch(ClearGames)
    }, Seq())

    def refreshData(): Unit = dispatch(TryGetGamesTop(twitchCred.clientId, twitchCred.token))
    def fetchNextGames(pagination: String): Unit =
      dispatch(TryGetNextGamesTop(twitchCred.clientId, twitchCred.token, pagination))

    def prettyGame(game: GameData) =
      div(
        key := game.id,
        className := "card card-10 mb-2",
        img(src := game.box_art_url.replace("{width}", "285").replace("{height}", "380"), className := "card-img-top")
      )

    def prettyFetchMore(): ReactElement = {
      val onClick = games
        .fold(none[String])(_.pagination.map(_.cursor))
        .map(cursor => () => fetchNextGames(cursor))
      FetchMoreButton(nextGames, onClick)
    }

    div(
      h1(
        "Games",
        button(className := "ml-2 btn btn-primary", "refresh", onClick := (refreshData _))
      ),
      div(
        games.state match {
          case PotEmpty =>
            div("nothing here")
          case PotPending =>
            div(
              className := "spinner-border text-primary",
              role := "status",
              span(className := "sr-only", "Loading...")
            )
          case PotFailed =>
            games.exceptionOption.fold("unknown error")(msg => " error: " + msg.getMessage())
          case PotReady =>
            games.fold(div("unknown error"): ReactElement) { gs =>
              Fragment(
                div(className := "card-deck", gs.data.map(prettyGame)),
                prettyFetchMore()
              )
            }
          case _ => div()
        }
      )
    )
  }
}
