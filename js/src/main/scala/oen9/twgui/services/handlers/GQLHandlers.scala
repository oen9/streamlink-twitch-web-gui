package oen9.twgui.services.handlers

import diode.ActionHandler
import diode.data.Pot
import diode.data.PotAction
import diode.ModelRW
import oen9.twgui.services.ajax.GQLClient
import oen9.twgui.services.CircuitActions._
import scala.concurrent.ExecutionContext.Implicits.global

class PlayStreamHandler[M](modelRW: ModelRW[M, Pot[Option[Boolean]]]) extends ActionHandler(modelRW) {
  override def handle = {
    case action: TryPlayStream =>
      val updateF = action.effect(GQLClient.playStream(action.name))(identity _)
      action.handleWith(this, updateF)(PotAction.handler())
  }
}
