package oen9.twgui.services.handlers

import diode.Action
import diode.ActionHandler
import diode.data.Empty
import diode.data.Pot
import diode.data.PotAction
import diode.ModelRW
import diode.NoAction
import oen9.twgui.services.ajax.TwitchClient
import oen9.twgui.services.ajax.TwitchData.FeaturedStreams
import oen9.twgui.services.ajax.TwitchData.Games
import oen9.twgui.services.ajax.TwitchData.Streams
import oen9.twgui.services.ajax.TwitchData.StreamsFollowed
import oen9.twgui.services.ajax.TwitchData.UserData
import oen9.twgui.services.CircuitActions._
import oen9.twgui.services.Followers
import scala.concurrent.ExecutionContext.Implicits.global

class StreamsFollowedHandler[M](modelRW: ModelRW[M, Pot[StreamsFollowed]]) extends ActionHandler(modelRW) {
  override def handle = {
    case action: TryGetStreamsFollowed =>
      val updateF = action.effect(TwitchClient.getStreamsFollowed(action.clientId, action.token))(identity _)
      action.handleWith(this, updateF)(PotAction.handler())

    case ClearStreamsFollowed => updated(Empty)
  }
}

class StreamsHandler[M](modelRW: ModelRW[M, Pot[Streams]]) extends ActionHandler(modelRW) {
  override def handle = {
    case action: TryGetStreams =>
      val updateF = action.effect(TwitchClient.getStreams(action.clientId, action.token))(identity _)
      val onReady: PotAction[Streams, TryGetStreams] => Action =
        _.potResult.fold(NoAction: Action)(streams =>
          TryGetGames(action.clientId, action.token, streams.data.map(_.game_id))
        )

      action.handleWith(this, updateF)(GenericHandlers.withOnReady(onReady))

    case ClearStreams => updated(Empty)
  }
}

class GamesHandler[M](modelRW: ModelRW[M, Pot[Games]]) extends ActionHandler(modelRW) {
  override def handle = {
    case action: TryGetGames =>
      val updateF = action.effect(TwitchClient.getGames(action.clientId, action.token, action.ids))(identity _)
      action.handleWith(this, updateF)(PotAction.handler())

    case action: TryGetGamesTop =>
      val updateF = action.effect(TwitchClient.getGamesTop(action.clientId, action.token))(identity _)
      action.handleWith(this, updateF)(PotAction.handler())

    case CombineGames(toCombine) =>
      val newValue = value.map { oldGames =>
        Games(
          data = oldGames.data ++ toCombine.data,
          pagination = toCombine.pagination
        )
      }
      updated(newValue)

    case ClearGames => updated(Empty)
  }
}

class NextGamesHandler[M](modelRW: ModelRW[M, Pot[Games]]) extends ActionHandler(modelRW) {
  override def handle = {
    case action: TryGetNextGamesTop =>
      val updateF =
        action.effect(TwitchClient.getTopGamesAfter(action.clientId, action.token, action.pagination))(identity _)
      val onReady: PotAction[Games, TryGetNextGamesTop] => Action =
        _.potResult.fold(NoAction: Action)(games => CombineGames(games))
      action.handleWith(this, updateF)(GenericHandlers.withOnReady(onReady))
  }
}

class FeaturedStreamsHandler[M](modelRW: ModelRW[M, Pot[FeaturedStreams]]) extends ActionHandler(modelRW) {
  override def handle = {
    case action: TryGetFeaturedStreams =>
      val updateF = action.effect(TwitchClient.getStreamsFeatured(action.clientId, action.token))(identity _)
      action.handleWith(this, updateF)(PotAction.handler())

    case ClearFeaturedStreams => updated(Empty)
  }
}

class MeHandler[M](modelRW: ModelRW[M, Pot[UserData]]) extends ActionHandler(modelRW) {
  override def handle = {
    case action: TryGetMe =>
      val updateF =
        action.effect(TwitchClient.getUsers(action.clientId, action.token))(_.data.headOption.getOrElse(UserData()))
      action.handleWith(this, updateF)(PotAction.handler())
  }
}

class FollowedChannelsHandler[M](modelRW: ModelRW[M, Pot[Followers]]) extends ActionHandler(modelRW) {
  override def handle = {
    case action: TryGetFollowedChannels =>
      def fetchData() =
        for {
          follows  <- TwitchClient.getUsersFollows(action.clientId, action.token, action.meId)
          channels <- TwitchClient.getUsers(action.clientId, action.token, follows.data.map(_.to_id))
        } yield Followers(follows.total, channels.data, follows.pagination)

      val updateF = action.effect(fetchData())(identity _)
      action.handleWith(this, updateF)(PotAction.handler())

    case ClearFollowedChannels => updated(Empty)
  }
}
