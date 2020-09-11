package oen9.twgui.services

import diode.Circuit
import diode.data.Empty
import diode.data.Pot
import oen9.twgui.services.ajax.TwitchData.Games
import oen9.twgui.services.ajax.TwitchData.Streams
import oen9.twgui.services.ajax.TwitchData.StreamsFollowed
import oen9.twgui.services.handlers.GamesHandler
import oen9.twgui.services.handlers.StreamsFollowedHandler
import oen9.twgui.services.handlers.StreamsHandler
import oen9.twgui.services.handlers.TwitchCredHandler

case class TwitchCred(clientId: String = "", token: String = "")
case class RootModel(
  twitchCred: TwitchCred = TwitchCred(),
  streamsFollowed: Pot[StreamsFollowed] = Empty,
  streams: Pot[Streams] = Empty,
  games: Pot[Games] = Empty
)

object AppCircuit extends Circuit[RootModel] {
  override protected def initialModel: RootModel = {
    val twitchCred = Storage.readTwitchCred()
    RootModel(twitchCred = twitchCred)
  }

  override protected def actionHandler: AppCircuit.HandlerFunction = composeHandlers(
    new TwitchCredHandler(zoomTo(_.twitchCred)),
    new StreamsFollowedHandler(zoomTo(_.streamsFollowed)),
    new StreamsHandler(zoomTo(_.streams)),
    new GamesHandler(zoomTo(_.games))
  )
}
