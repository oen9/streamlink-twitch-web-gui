package oen9.twgui.modules

import cats.implicits._
import diode.data.PotState.PotEmpty
import diode.data.PotState.PotFailed
import diode.data.PotState.PotPending
import diode.data.PotState.PotReady
import oen9.twgui.components.FetchMoreButton
import oen9.twgui.services.ajax.TwitchData.StreamFollowed
import oen9.twgui.services.AppCircuit
import oen9.twgui.services.CircuitActions.ClearStreamsFollowed
import oen9.twgui.services.CircuitActions.TryGetNextStreamsFollowed
import oen9.twgui.services.CircuitActions.TryGetStreamsFollowed
import oen9.twgui.services.CircuitActions.TryPlayStream
import oen9.twgui.services.KrakenPagination
import oen9.twgui.services.ReactDiode
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import slinky.core.facade.Hooks._
import slinky.core.facade.ReactElement
import slinky.core.FunctionalComponent
import slinky.web.html._

@react object FollowedStreams {
  type Props = Unit

  val component = FunctionalComponent[Props] { _ =>
    val (streamsFollowed, dispatch) = ReactDiode.useDiode(AppCircuit.zoom(_.streamsFollowed))
    val (nextStreamsFollowed, _)    = ReactDiode.useDiode(AppCircuit.zoom(_.nextStreamsFollowed))
    val (twitchCred, _)             = ReactDiode.useDiode(AppCircuit.zoom(_.twitchCred))

    useEffect(() => {
      refreshData()
      () => dispatch(ClearStreamsFollowed)
    }, Seq())

    def refreshData(): Unit            = dispatch(TryGetStreamsFollowed(twitchCred.clientId, twitchCred.token))
    def playStream(name: String): Unit = dispatch(TryPlayStream(name))
    def fetchNextStreams(limit: Int, offset: Int): Unit =
      dispatch(
        TryGetNextStreamsFollowed(
          clientId = twitchCred.clientId,
          token = twitchCred.token,
          limit = limit,
          offset = offset
        )
      )

    def prettyStreamFollowed(sf: StreamFollowed) =
      div(
        key := sf.channel.name,
        className := "card card-14 mb-2",
        img(src := sf.preview.large, className := "card-img-top"),
        h5(className := "card-header", sf.channel.display_name),
        div(
          className := "card-body",
          h5(className := "card-title", sf.game),
          p(className := "card-text", small(sf.channel.status))
        ),
        div(
          className := "card-footer",
          button(className := "btn btn-primary", "play", onClick := (() => playStream(sf.channel.name)))
        )
      )

    def prettyFetchMore(): ReactElement = {
      val onClick = streamsFollowed
        .fold(none[KrakenPagination]) { psf =>
          if (psf.pagination.limit == 0) none
          else psf.pagination.some
        }
        .map(pag => () => fetchNextStreams(pag.limit, pag.offset))
      FetchMoreButton(nextStreamsFollowed, onClick)
    }

    div(
      h1(
        "Followed Streams",
        button(className := "ml-2 btn btn-primary", "refresh", onClick := (refreshData _))
      ),
      div(
        streamsFollowed.state match {
          case PotEmpty =>
            div("nothing here")
          case PotPending =>
            div(
              className := "spinner-border text-primary",
              role := "status",
              span(className := "sr-only", "Loading...")
            )
          case PotFailed =>
            streamsFollowed.exceptionOption.fold("unknown error")(msg => " error: " + msg.getMessage())
          case PotReady =>
            streamsFollowed.fold(div("unknown error"): ReactElement) { ssf =>
              Fragment(
                div(className := "card-deck", ssf.streamsFollowed.streams.map(prettyStreamFollowed)),
                prettyFetchMore()
              )
            }
          case _ => div()
        }
      )
    )
  }
}
