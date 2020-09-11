package oen9.twgui.services.handlers

import diode.ActionType
import diode.data._
import diode.Effect
import diode.{ActionHandler}
import scala.concurrent.ExecutionContext.Implicits.global

object GenericHandlers {
  def withOnReady[A, M, P <: PotAction[A, P], D: ActionType](onReady: PotAction[A, P] => D) =
    (action: PotAction[A, P], handler: ActionHandler[M, Pot[A]], updateEffect: Effect) => {
      import PotState._
      import handler._

      action.state match {
        case PotEmpty =>
          updated(value.pending(), updateEffect)
        case PotPending =>
          noChange
        case PotReady =>
          val eff = Effect.action(onReady(action))
          updated(action.potResult, eff)
        case PotUnavailable =>
          updated(value.unavailable())
        case PotFailed =>
          val ex = action.result.failed.get
          updated(value.fail(ex))
      }
    }
}
