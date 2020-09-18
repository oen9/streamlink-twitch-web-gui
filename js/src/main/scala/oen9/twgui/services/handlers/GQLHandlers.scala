package oen9.twgui.services.handlers

import diode.Action
import diode.ActionHandler
import diode.data.Empty
import diode.data.Pot
import diode.data.PotAction
import diode.ModelRW
import oen9.twgui.services.ajax.GQLClient
import oen9.twgui.services.CircuitActions._
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global

class PlayStreamHandler[M](modelRW: ModelRW[M, Pot[Option[Boolean]]]) extends ActionHandler(modelRW) {
  override def handle = {
    case action: TryPlayStream =>
      val updateF = action.effect(GQLClient.playStream(action.name))(identity _)
      val onReady: PotAction[Option[Boolean], TryPlayStream] => Action = _ => TryGetLiveVideos()

      action.handleWith(this, updateF)(GenericHandlers.withOnReady(onReady, 2.second))
  }
}

class CloseStreamHandler[M](modelRW: ModelRW[M, Pot[Option[Boolean]]]) extends ActionHandler(modelRW) {
  override def handle = {
    case action: TryCloseStream =>
      val updateF = action.effect(GQLClient.closeStream(action.name))(identity _)
      val onReady: PotAction[Option[Boolean], TryCloseStream] => Action = _ => TryGetLiveVideos()

      action.handleWith(this, updateF)(GenericHandlers.withOnReady(onReady, 2.second))
  }
}

class LiveVideosHandler[M](modelRW: ModelRW[M, Pot[Set[String]]]) extends ActionHandler(modelRW) {
  override def handle = {
    case action: TryGetLiveVideos =>
      val updateF = action.effect(GQLClient.getLiveVideos)(identity _)
      action.handleWith(this, updateF)(PotAction.handler())
  }
}
