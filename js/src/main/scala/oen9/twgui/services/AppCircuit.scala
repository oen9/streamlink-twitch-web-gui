package oen9.twgui.services

import diode.Circuit
import diode.data.Empty
import diode.data.Pot
import diode.data.Ready
import oen9.twgui.services.ajax.TwitchData.FeaturedStreams
import oen9.twgui.services.ajax.TwitchData.Games
import oen9.twgui.services.ajax.TwitchData.Pagination
import oen9.twgui.services.ajax.TwitchData.Streams
import oen9.twgui.services.ajax.TwitchData.StreamsFollowed
import oen9.twgui.services.ajax.TwitchData.UserData
import oen9.twgui.services.handlers._

case class TwitchCred(clientId: String = "", token: String = "")
case class StreamlinkConfig(params: String = "")
case class Followers(total: Int, data: Seq[UserData], pagination: Pagination)
case class KrakenPagination(limit: Int = 5, offset: Int = 0)
case class PaginatedStreamsFollowed(
  streamsFollowed: StreamsFollowed = StreamsFollowed(),
  pagination: KrakenPagination = KrakenPagination()
)
case class RootModel(
  twitchCred: TwitchCred = TwitchCred(),
  streamsFollowed: Pot[PaginatedStreamsFollowed] = Empty,
  nextStreamsFollowed: Pot[PaginatedStreamsFollowed] = Ready(PaginatedStreamsFollowed()),
  streams: Pot[Streams] = Empty,
  nextStreams: Pot[Streams] = Ready(Streams()),
  games: Pot[Games] = Empty,
  nextGames: Pot[Games] = Ready(Games()),
  featuredStreams: Pot[FeaturedStreams] = Empty,
  me: Pot[UserData] = Empty,
  followers: Pot[Followers] = Empty,
  lastStreamOperation: Pot[Option[Boolean]] = Empty,
  liveVideos: Pot[Set[String]] = Empty,
  streamlinkConfig: StreamlinkConfig = StreamlinkConfig()
)

object AppCircuit extends Circuit[RootModel] {
  override protected def initialModel: RootModel = {
    val twitchCred = Storage.readTwitchCred()
    RootModel(twitchCred = twitchCred)
  }

  override protected def actionHandler: AppCircuit.HandlerFunction = composeHandlers(
    new TwitchCredHandler(zoomTo(_.twitchCred)),
    new StreamsFollowedHandler(zoomTo(_.streamsFollowed)),
    new NextStreamsFollowedHandler(zoomTo(_.nextStreamsFollowed)),
    new StreamsHandler(zoomTo(_.streams)),
    new NextStreamsHandler(zoomTo(_.nextStreams)),
    new GamesHandler(zoomTo(_.games)),
    new NextGamesHandler(zoomTo(_.nextGames)),
    new FeaturedStreamsHandler(zoomTo(_.featuredStreams)),
    new MeHandler(zoomTo(_.me)),
    new FollowedChannelsHandler(zoomTo(_.followers)),
    new PlayStreamHandler(zoomTo(_.lastStreamOperation)),
    new CloseStreamHandler(zoomTo(_.lastStreamOperation)),
    new LiveVideosHandler(zoomTo(_.liveVideos)),
    new StreamlinkConfigHandler(zoomTo(_.streamlinkConfig))
  )
}

object KrakenPagination {
  def next(oldLimit: Int, oldOffset: Int, nextSize: Int): KrakenPagination =
    next(KrakenPagination(oldLimit, oldOffset), nextSize)
  def next(old: KrakenPagination, nextSize: Int): KrakenPagination =
    KrakenPagination(
      limit = if (nextSize < old.limit) 0 else old.limit,
      offset = old.offset + old.limit
    )
}
