package oen9.twgui.gql

object GQLData {
  case class TwitchConfig(clientId: String, token: String)
  case class Config(twitch: TwitchConfig)

  case class Queries(config: Config)
}
