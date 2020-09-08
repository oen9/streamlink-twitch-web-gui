package oen9.twgui.modules

import org.scalajs.dom.{html, Event}
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.core.FunctionalComponent
import slinky.core.SyntheticEvent
import slinky.web.html._

@react object Settings {
  type Props = Unit

  def handleSave(e: SyntheticEvent[html.Form, Event]): Unit = {
    e.preventDefault()
    println("saved")
  }

  def settingsForm(): ReactElement =
    form(
      onSubmit := (handleSave(_)),
      div(
        className := "form-group",
        label(htmlFor := "clientId", "Client ID"),
        input(`type` := "text", className := "form-control", id := "clientId", aria - "describedby" := "clientIdHelp"),
        small(id := "clientIdHelp", className := "form-text text-muted", "twitch application Client ID")
      ),
      div(
        className := "form-group",
        label(htmlFor := "twitchToken", "token"),
        input(
          `type` := "text",
          className := "form-control",
          id := "twitchToken",
          aria - "describedby" := "twitchTokenHelp"
        ),
        small(id := "twitchTokenHelp", className := "form-text text-muted", "twitch token")
      ),
      button(`type` := "submit", className := "btn btn-primary", "Save")
    )

  val component = FunctionalComponent[Props] { _ =>
    div(
      h1("settings"),
      settingsForm()
    )
  }
}
