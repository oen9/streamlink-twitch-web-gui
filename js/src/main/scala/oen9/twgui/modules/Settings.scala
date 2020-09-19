package oen9.twgui.modules

import oen9.twgui.services.AppCircuit
import oen9.twgui.services.CircuitActions.SetStreamlinkConfig
import oen9.twgui.services.CircuitActions.SetTwitchCred
import oen9.twgui.services.ReactDiode
import oen9.twgui.services.StreamlinkConfig
import oen9.twgui.services.TwitchCred
import org.scalajs.dom.{html, Event}
import slinky.core.annotations.react
import slinky.core.facade.Hooks._
import slinky.core.facade.ReactElement
import slinky.core.FunctionalComponent
import slinky.core.SyntheticEvent
import slinky.web.html._

@react object Settings {
  type Props = Unit

  val component = FunctionalComponent[Props] { _ =>
    val (twitchCred, dispatch)  = ReactDiode.useDiode(AppCircuit.zoom(_.twitchCred))
    val (clientId, setClientId) = useState("")
    val (token, setToken)       = useState("")
    val (streamlinkConfig, _)   = ReactDiode.useDiode(AppCircuit.zoom(_.streamlinkConfig))
    val (params, setParams)     = useState("")

    useEffect(() => {
      setClientId(twitchCred.clientId)
      setToken(twitchCred.token)
    }, Seq(twitchCred))

    useEffect(() => {
      setParams(streamlinkConfig.params)
    }, Seq(streamlinkConfig))

    def handleSaveTwitchSettings(e: SyntheticEvent[html.Form, Event]): Unit = {
      e.preventDefault()
      dispatch(SetTwitchCred(TwitchCred(clientId, token)))
    }
    def handleSaveStreamlinkSettings(e: SyntheticEvent[html.Form, Event]): Unit = {
      e.preventDefault()
      dispatch(SetStreamlinkConfig(StreamlinkConfig(params)))
    }

    def onChangeClientId(e: SyntheticEvent[html.Input, Event]): Unit = setClientId(e.target.value)
    def onChangeToken(e: SyntheticEvent[html.Input, Event]): Unit    = setToken(e.target.value)
    def onChangeParams(e: SyntheticEvent[html.Input, Event]): Unit   = setParams(e.target.value)

    def twitchSettingsForm(): ReactElement =
      form(
        onSubmit := (handleSaveTwitchSettings(_)),
        h2(className := "mt-2", "Twitch"),
        div(
          className := "form-group",
          label(htmlFor := "clientId", "Client ID"),
          input(
            `type` := "text",
            className := "form-control",
            id := "clientId",
            aria - "describedby" := "clientIdHelp",
            value := clientId,
            onChange := (onChangeClientId(_))
          ),
          small(id := "clientIdHelp", className := "form-text text-muted", "twitch application Client ID")
        ),
        div(
          className := "form-group",
          label(htmlFor := "twitchToken", "token"),
          input(
            `type` := "text",
            className := "form-control",
            id := "twitchToken",
            aria - "describedby" := "twitchTokenHelp",
            value := token,
            onChange := (onChangeToken(_))
          ),
          small(id := "twitchTokenHelp", className := "form-text text-muted", "twitch token")
        ),
        button(`type` := "submit", className := "btn btn-primary", "Save")
      )

    def streamlinkSettingsForm(): ReactElement =
      form(
        onSubmit := (handleSaveStreamlinkSettings(_)),
        h2(className := "mt-2", "Streamlink"),
        div(
          className := "form-group",
          label(htmlFor := "params", "params"),
          input(
            `type` := "text",
            className := "form-control",
            id := "params",
            aria - "describedby" := "paramsHelp",
            value := params,
            onChange := (onChangeParams(_))
          ),
          small(
            id := "paramsHelp",
            className := "form-text text-muted",
            """Streamlink commandline params. e.g. for `-p "vlc"` we run `streamlink -p "vlc" twitch.tv/someone best`"""
          )
        ),
        button(`type` := "submit", className := "btn btn-primary", "Save")
      )

    div(
      h1("Settings"),
      twitchSettingsForm(),
      streamlinkSettingsForm()
    )
  }
}
