package oen9.twgui.services.handlers

import diode.{ActionHandler, ModelRW}
import oen9.twgui.services.CircuitActions.SetTwitchCred
import oen9.twgui.services.Storage
import oen9.twgui.services.TwitchCred

class TwitchCredHandler[M](modelRW: ModelRW[M, TwitchCred]) extends ActionHandler(modelRW) {
  override def handle = {
    case SetTwitchCred(newCred) =>
      Storage.saveTwitchCred(newCred)
      updated(newCred)
  }
}
