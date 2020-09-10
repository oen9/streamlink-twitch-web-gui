package oen9.twgui.modules

import oen9.twgui.services.AppCircuit
import oen9.twgui.services.CircuitActions.SetTwitchCred
import oen9.twgui.services.ReactDiode
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

    useEffect(() => {
      setClientId(twitchCred.clientId)
      setToken(twitchCred.token)
    }, Seq())

    def handleSave(e: SyntheticEvent[html.Form, Event]): Unit = {
      e.preventDefault()
      dispatch(SetTwitchCred(TwitchCred(clientId, token)))
    }

    def onChangeClientId(e: SyntheticEvent[html.Input, Event]): Unit = setClientId(e.target.value)
    def onChangeToken(e: SyntheticEvent[html.Input, Event]): Unit    = setToken(e.target.value)

    def settingsForm(): ReactElement =
      form(
        onSubmit := (handleSave(_)),
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

    div(
      h1("Settings"),
      settingsForm()
    )
  }
}
