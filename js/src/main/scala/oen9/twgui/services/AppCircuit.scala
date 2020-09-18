package oen9.twgui.services

import diode.Circuit
import diode.data.Empty
import diode.data.Pot
import oen9.twgui.services.ajax.TwitchData.FeaturedStreams
import oen9.twgui.services.ajax.TwitchData.Games
import oen9.twgui.services.ajax.TwitchData.Pagination
import oen9.twgui.services.ajax.TwitchData.Streams
import oen9.twgui.services.ajax.TwitchData.StreamsFollowed
import oen9.twgui.services.ajax.TwitchData.UserData
import oen9.twgui.services.handlers._

case class TwitchCred(clientId: String = "", token: String = "")
case class Followers(total: Int, data: Seq[UserData], pagination: Pagination)
case class RootModel(
  twitchCred: TwitchCred = TwitchCred(),
  streamsFollowed: Pot[StreamsFollowed] = Empty,
  streams: Pot[Streams] = Empty,
  games: Pot[Games] = Empty,
  featuredStreams: Pot[FeaturedStreams] = Empty,
  me: Pot[UserData] = Empty,
  followers: Pot[Followers] = Empty,
  lastStreamOperation: Pot[Option[Boolean]] = Empty,
  liveVideos: Pot[Set[String]] = Empty
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
    new GamesHandler(zoomTo(_.games)),
    new FeaturedStreamsHandler(zoomTo(_.featuredStreams)),
    new MeHandler(zoomTo(_.me)),
    new FollowedChannelsHandler(zoomTo(_.followers)),
    new PlayStreamHandler(zoomTo(_.lastStreamOperation)),
    new CloseStreamHandler(zoomTo(_.lastStreamOperation)),
    new LiveVideosHandler(zoomTo(_.liveVideos))
  )
}
