package oen9.twgui.services

import diode.{Action, Circuit}
import oen9.twgui.services.handlers.TwitchCredHandler

case class TwitchCred(clientId: String = "", token: String = "")
case class RootModel(
  twitchCred: TwitchCred = TwitchCred()
)

case class SetTwitchCred(newCred: TwitchCred) extends Action

object AppCircuit extends Circuit[RootModel] {
  override protected def initialModel: RootModel = {
    val twitchCred = Storage.readTwitchCred()
    RootModel(twitchCred = twitchCred)
  }

  override protected def actionHandler: AppCircuit.HandlerFunction = composeHandlers(
    new TwitchCredHandler(zoomTo(_.twitchCred))
  )
}
