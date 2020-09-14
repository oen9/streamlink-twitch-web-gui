package oen9.twgui.services.handlers

import diode.Effect
import diode.{ActionHandler, ModelRW}
import oen9.twgui.services.ajax.GQLClient
import oen9.twgui.services.CircuitActions.GetDefaultTwitchCred
import oen9.twgui.services.CircuitActions.GotDefaultTwitchCred
import oen9.twgui.services.CircuitActions.SetTwitchCred
import oen9.twgui.services.CircuitActions.TryGetMe
import oen9.twgui.services.Storage
import oen9.twgui.services.TwitchCred
import scala.concurrent.ExecutionContext.Implicits.global

class TwitchCredHandler[M](modelRW: ModelRW[M, TwitchCred]) extends ActionHandler(modelRW) {
  override def handle = {
    case SetTwitchCred(newCred) =>
      Storage.saveTwitchCred(newCred)
      updated(newCred, Effect.action(TryGetMe(newCred.clientId, newCred.token)))

    case GetDefaultTwitchCred =>
      val fetchTwitchCred = Effect(GQLClient.getTwitchConfig.map(tc => GotDefaultTwitchCred(tc)))
      effectOnly(fetchTwitchCred)

    case GotDefaultTwitchCred(defaultCred) =>
      updated(defaultCred, Effect.action(TryGetMe(defaultCred.clientId, defaultCred.token)))
  }
}
