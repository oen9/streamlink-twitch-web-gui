package oen9.twgui.modules

import diode.data.PotState.PotEmpty
import diode.data.PotState.PotFailed
import diode.data.PotState.PotPending
import diode.data.PotState.PotReady
import oen9.twgui.services.AppCircuit
import oen9.twgui.services.CircuitActions.TryCloseStream
import oen9.twgui.services.CircuitActions.TryGetLiveVideos
import oen9.twgui.services.ReactDiode
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.core.FunctionalComponent
import slinky.web.html._

@react object LiveVideos {
  type Props = Unit

  val component = FunctionalComponent[Props] { _ =>
    val (liveVideos, dispatch) = ReactDiode.useDiode(AppCircuit.zoom(_.liveVideos))

    def refreshData(): Unit             = dispatch(TryGetLiveVideos())
    def closeStream(name: String): Unit = dispatch(TryCloseStream(name))

    def prettyLiveStream(liveStream: String) =
      div(
        key := liveStream,
        className := "card card-14 mb-2",
        h5(className := "card-header", liveStream),
        div(
          className := "card-body",
          h5(className := "card-title", liveStream)
        ),
        div(
          className := "card-footer",
          button(className := "btn btn-primary", "close", onClick := (() => closeStream(liveStream)))
        )
      )

    div(
      h1(
        "Live videos",
        button(className := "ml-2 btn btn-primary", "refresh", onClick := (refreshData _))
      ),
      div(
        liveVideos.state match {
          case PotEmpty =>
            div("nothing here")
          case PotPending =>
            div(
              className := "spinner-border text-primary",
              role := "status",
              span(className := "sr-only", "Loading...")
            )
          case PotFailed =>
            liveVideos.exceptionOption.fold("unknown error")(msg => " error: " + msg.getMessage())
          case PotReady =>
            liveVideos.fold(div("unknown error"): ReactElement) { lv =>
              div(className := "card-deck", lv match {
                case s if s.isEmpty => div("There is no video")
                case names          => names.map(prettyLiveStream)
              })
            }
          case _ => div()
        }
      )
    )
  }
}
