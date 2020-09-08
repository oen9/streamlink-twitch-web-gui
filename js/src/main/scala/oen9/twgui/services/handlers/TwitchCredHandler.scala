package oen9.twgui.services.handlers

import diode.{ActionHandler, ModelRW}
import oen9.twgui.services.SetTwitchCred
import oen9.twgui.services.TwitchCred
import oen9.twgui.services.Storage

class TwitchCredHandler[M](modelRW: ModelRW[M, TwitchCred]) extends ActionHandler(modelRW) {
  override def handle = {
    case SetTwitchCred(newCred) =>
      Storage.saveTwitchCred(newCred)
      updated(newCred)
  }
}
