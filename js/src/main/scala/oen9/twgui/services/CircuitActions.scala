package oen9.twgui.services

import diode.Action
import diode.data.Empty
import diode.data.Pot
import diode.data.PotAction
import oen9.twgui.services.ajax.TwitchData.FeaturedStreams
import oen9.twgui.services.ajax.TwitchData.Games
import oen9.twgui.services.ajax.TwitchData.Streams
import oen9.twgui.services.ajax.TwitchData.StreamsFollowed

object CircuitActions {
  case class SetTwitchCred(newCred: TwitchCred) extends Action

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

  case class TryGetFeaturedStreams(clientId: String, token: String, potResult: Pot[FeaturedStreams] = Empty)
      extends PotAction[FeaturedStreams, TryGetFeaturedStreams] {
    def next(newResult: Pot[FeaturedStreams]) = copy(potResult = newResult)
  }
  case object ClearFeaturedStreams extends Action
}
