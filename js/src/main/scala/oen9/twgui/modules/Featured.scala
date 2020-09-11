package oen9.twgui.modules

import diode.data.PotState.PotEmpty
import diode.data.PotState.PotFailed
import diode.data.PotState.PotPending
import diode.data.PotState.PotReady
import oen9.twgui.services.ajax.TwitchData.FeaturedS
import oen9.twgui.services.AppCircuit
import oen9.twgui.services.CircuitActions.ClearFeaturedStreams
import oen9.twgui.services.CircuitActions.TryGetFeaturedStreams
import oen9.twgui.services.ReactDiode
import slinky.core.annotations.react
import slinky.core.facade.Hooks._
import slinky.core.facade.ReactElement
import slinky.core.FunctionalComponent
import slinky.web.html._

@react object Featured {
  type Props = Unit

  val component = FunctionalComponent[Props] { _ =>
    val (featuredStreams, dispatch)         = ReactDiode.useDiode(AppCircuit.zoom(_.featuredStreams))
    val (twitchCred, _)                     = ReactDiode.useDiode(AppCircuit.zoom(_.twitchCred))
    val (selectedStream, setSelectedStream) = useState(0)

    useEffect(() => {
      refreshData()
      () => dispatch(ClearFeaturedStreams)
    }, Seq())

    def refreshData(): Unit = dispatch(TryGetFeaturedStreams(twitchCred.clientId, twitchCred.token))

    def prettyFeaturedTop(feat: FeaturedS) = {
      val stream  = feat.stream
      val channel = stream.channel
      val preview = stream.preview
      div(
        className := "row mb-4",
        div(
          className := "col col-m-6 text-center",
          img(src := preview.template.replace("{width}", "440").replace("{height}", "248"))
        ),
        div(
          className := "col col-m-6",
          h1(channel.display_name),
          div(className := "row", div(className := "col", "game"), div(className := "col", channel.game)),
          div(className := "row", div(className := "col", "viewers"), div(className := "col", stream.viewers))
        )
      )
    }

    def prettyFeaturedBottom(feat: FeaturedS, idx: Int) =
      div(
        key := feat.stream.channel.display_name,
        className := "card card-10 mb-2",
        img(
          src := feat.stream.preview.template.replace("{width}", "440").replace("{height}", "248"),
          className := "card-img-top",
          onClick := (() => setSelectedStream(idx))
        )
      )

    div(
      h1(
        "Featured",
        button(className := "ml-2 btn btn-primary", "refresh", onClick := (refreshData _))
      ),
      div(
        featuredStreams.state match {
          case PotEmpty =>
            div("nothing here")
          case PotPending =>
            div(
              className := "spinner-border text-primary",
              role := "status",
              span(className := "sr-only", "Loading...")
            )
          case PotFailed =>
            featuredStreams.exceptionOption.fold("unknown error")(msg => " error: " + msg.getMessage())
          case PotReady =>
            featuredStreams.fold(div("unknown error"): ReactElement) { fs =>
              div(
                fs.featured.lift(selectedStream).map(prettyFeaturedTop),
                div(className := "card-deck", fs.featured.zipWithIndex.map {
                  case (feat, idx) => prettyFeaturedBottom(feat, idx)
                })
              )
            }
          case _ => div()
        }
      )
    )
  }
}
