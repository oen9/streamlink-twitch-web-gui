package oen9.twgui.services

import diode.Action
import diode.data.Empty
import diode.data.Pot
import diode.data.PotAction
import oen9.twgui.services.ajax.TwitchData.FeaturedStreams
import oen9.twgui.services.ajax.TwitchData.Games
import oen9.twgui.services.ajax.TwitchData.Streams
import oen9.twgui.services.ajax.TwitchData.StreamsFollowed
import oen9.twgui.services.ajax.TwitchData.UserData

object CircuitActions {
  case class SetTwitchCred(newCred: TwitchCred)            extends Action
  case object GetDefaultTwitchCred                         extends Action
  case class GotDefaultTwitchCred(defaultCred: TwitchCred) extends Action
  case class SetStreamlinkConfig(newCfg: StreamlinkConfig) extends Action
  case object GetStreamlinkConfig                          extends Action
  case class GotStreamlinkConfig(cfg: StreamlinkConfig)    extends Action

  case class TryGetMe(clientId: String, token: String, potResult: Pot[UserData] = Empty)
      extends PotAction[UserData, TryGetMe] {
    def next(newResult: Pot[UserData]) = copy(potResult = newResult)
  }

  case class TryGetStreamsFollowed(clientId: String, token: String, potResult: Pot[StreamsFollowed] = Empty)
      extends PotAction[StreamsFollowed, TryGetStreamsFollowed] {
    def next(newResult: Pot[StreamsFollowed]) = copy(potResult = newResult)
  }
  case object ClearStreamsFollowed extends Action

  case class TryGetStreams(clientId: String, token: String, potResult: Pot[Streams] = Empty)
      extends PotAction[Streams, TryGetStreams] {
    def next(newResult: Pot[Streams]) = copy(potResult = newResult)
  }
  case object ClearStreams extends Action

  case class TryGetGames(clientId: String, token: String, ids: Seq[String], potResult: Pot[Games] = Empty)
      extends PotAction[Games, TryGetGames] {
    def next(newResult: Pot[Games]) = copy(potResult = newResult)
  }

  case class TryGetGamesTop(clientId: String, token: String, potResult: Pot[Games] = Empty)
      extends PotAction[Games, TryGetGamesTop] {
    def next(newResult: Pot[Games]) = copy(potResult = newResult)
  }
  case object ClearGames extends Action

  case class TryGetNextGamesTop(clientId: String, token: String, pagination: String, potResult: Pot[Games] = Empty)
      extends PotAction[Games, TryGetNextGamesTop] {
    def next(newResult: Pot[Games]) = copy(potResult = newResult)
  }
  case class CombineGames(games: Games) extends Action

  case class TryGetFeaturedStreams(clientId: String, token: String, potResult: Pot[FeaturedStreams] = Empty)
      extends PotAction[FeaturedStreams, TryGetFeaturedStreams] {
    def next(newResult: Pot[FeaturedStreams]) = copy(potResult = newResult)
  }
  case object ClearFeaturedStreams extends Action

  case class TryGetFollowedChannels(clientId: String, token: String, meId: Int, potResult: Pot[Followers] = Empty)
      extends PotAction[Followers, TryGetFollowedChannels] {
    def next(newResult: Pot[Followers]) = copy(potResult = newResult)
  }
  case object ClearFollowedChannels extends Action

  case class TryPlayStream(name: String, potResult: Pot[Option[Boolean]] = Empty)
      extends PotAction[Option[Boolean], TryPlayStream] {
    def next(newResult: Pot[Option[Boolean]]) = copy(potResult = newResult)
  }
  case class TryCloseStream(name: String, potResult: Pot[Option[Boolean]] = Empty)
      extends PotAction[Option[Boolean], TryCloseStream] {
    def next(newResult: Pot[Option[Boolean]]) = copy(potResult = newResult)
  }
  case class TryGetLiveVideos(potResult: Pot[Set[String]] = Empty) extends PotAction[Set[String], TryGetLiveVideos] {
    def next(newResult: Pot[Set[String]]) = copy(potResult = newResult)
  }
}
