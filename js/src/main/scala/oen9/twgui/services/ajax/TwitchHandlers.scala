package oen9.twgui.services.ajax

import diode.ActionHandler
import diode.data.Pot
import diode.data.PotAction
import diode.ModelRW
import oen9.twgui.services.ajax.TwitchData.StreamsFollowed
import oen9.twgui.services.CircuitActions._
import scala.concurrent.ExecutionContext.Implicits.global

class StreamsFollowedHandler[M](modelRW: ModelRW[M, Pot[StreamsFollowed]]) extends ActionHandler(modelRW) {
  override def handle = {
    case action: TryGetStreamsFollowed =>
      val updateF = action.effect(TwitchClient.getStreamsFollowed(action.clientId, action.token))(identity _)
      action.handleWith(this, updateF)(PotAction.handler())
  }
}
