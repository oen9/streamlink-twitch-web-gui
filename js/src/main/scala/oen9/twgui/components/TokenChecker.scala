package oen9.twgui.components

import cats.implicits._
import diode.data.PotState.PotEmpty
import diode.data.PotState.PotPending
import diode.data.PotState.PotReady
import oen9.twgui.services.AppCircuit
import oen9.twgui.services.ReactDiode
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.core.FunctionalComponent
import slinky.web.html._

@react object TokenChecker {
  type Props = Unit

  val component = FunctionalComponent[Props] { _ =>
    val (me, dispatch) = ReactDiode.useDiode(AppCircuit.zoom(_.me))

    val info = div(className := "alert alert-warning", "Please fix settings").some

    me.state match {
      case PotReady              => me.fold(info)(_ => none)
      case PotPending | PotEmpty => none[ReactElement]
      case _                     => info
    }
  }
}
