package oen9.twgui.gql

import oen9.twgui.Hello
import zio._

object GQLData {
  case class TwitchConfig(clientId: String, token: String)
  case class Config(twitch: TwitchConfig)

  case class PlayStreamArgs(name: String)

  case class Queries(config: Config)
  case class Mutations(playStream: PlayStreamArgs => ZIO[Hello.AppEnv, Throwable, Boolean])
}
