package oen9.twgui.components

import diode.data.Pot
import diode.data.PotState.PotPending
import slinky.core.annotations.react
import slinky.core.FunctionalComponent
import slinky.web.html._

@react object FetchMoreButton {
  case class Props(nextPot: Pot[Any], onClick: Option[() => Unit])

  val component = FunctionalComponent[Props] { props =>
    props.nextPot.state match {
      case PotPending =>
        div(
          className := "text-center",
          button(
            className := "ml-2 btn btn-primary",
            disabled := true,
            div(
              className := "spinner-border text-secondary",
              role := "status",
              span(className := "sr-only", "Loading...")
            )
          )
        )
      case _ =>
        props.onClick.map { onC =>
          div(
            className := "text-center",
            button(className := "ml-2 btn btn-primary", "fetch more", onClick := onC)
          )
        }
    }
  }
}
