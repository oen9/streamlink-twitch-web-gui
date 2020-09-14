package oen9.twgui.gql

import caliban.GraphQL.graphQL
import caliban.RootResolver
import oen9.twgui.gql.GQLData.Config
import oen9.twgui.gql.GQLData.Queries
import oen9.twgui.gql.GQLData.TwitchConfig
import oen9.twgui.modules.appConfig

class GQLResolver(appTwitchConfig: appConfig.Twitch) {
  val twitchConfig = TwitchConfig(appTwitchConfig.clientId, appTwitchConfig.token)

  def getConfig = Config(twitch = twitchConfig)

  val queries = Queries(config = getConfig)
  val api     = graphQL(RootResolver(queries))
}
