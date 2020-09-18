package oen9.twgui.services.handlers

import diode.ActionResult
import diode.ActionType
import diode.data._
import diode.Effect
import diode.{ActionHandler}
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration

object GenericHandlers {
  def withOnReady[A, M, P <: PotAction[A, P], D: ActionType](
    onReady: PotAction[A, P] => D
  ): (PotAction[A, P], ActionHandler[M, Pot[A]], Effect) => ActionResult[M] = withOnReady(onReady, Duration.Zero)

  def withOnReady[A, M, P <: PotAction[A, P], D: ActionType](
    onReady: PotAction[A, P] => D,
    onReadyDelay: FiniteDuration
  ): (PotAction[A, P], ActionHandler[M, Pot[A]], Effect) => ActionResult[M] =
    (action: PotAction[A, P], handler: ActionHandler[M, Pot[A]], updateEffect: Effect) => {
      import scala.concurrent.ExecutionContext.Implicits.global
      import diode.Implicits.runAfterImpl
      import PotState._
      import handler._

      action.state match {
        case PotEmpty =>
          updated(value.pending(), updateEffect)
        case PotPending =>
          noChange
        case PotReady =>
          val eff = Effect.action(onReady(action)).after(onReadyDelay)
          updated(action.potResult, eff)
        case PotUnavailable =>
          updated(value.unavailable())
        case PotFailed =>
          val ex = action.result.failed.get
          updated(value.fail(ex))
      }
    }
}
