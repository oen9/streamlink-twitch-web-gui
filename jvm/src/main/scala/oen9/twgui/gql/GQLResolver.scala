package oen9.twgui.gql

import caliban.GraphQL.graphQL
import caliban.RootResolver
import caliban.schema.GenericSchema
import oen9.twgui.gql.GQLData._
import oen9.twgui.Hello
import oen9.twgui.modules.appConfig
import oen9.twgui.modules.services.streamlinkService

class GQLResolver(appTwitchConfig: appConfig.Twitch) {
  val twitchConfig = TwitchConfig(appTwitchConfig.clientId, appTwitchConfig.token)

  def getStreamlinkConfig()            = streamlinkService.getParams().map(StreamlinkConfig)
  def getConfig = Config(
    twitch = twitchConfig,
    streamlink = getStreamlinkConfig()
  )

  def playStream(args: StreamNameArgs)            = streamlinkService.play(args.name).map(_ => true)
  def closeStream(args: StreamNameArgs)           = streamlinkService.close(args.name).map(_ => true)
  def setStreamlinkConfig(args: StreamlinkConfig) = streamlinkService.setParams(args.params).map(_ => true)
  def getLive() =
    for {
      liveNames <- streamlinkService.getLive()
    } yield liveNames.map(LiveVideo)

  val queries = Queries(
    config = getConfig,
    video = Video(
      live = getLive()
    )
  )
  val mutations = Mutations(streamlink =
    StreamlinkMut(
      video = VideoMut(
        playStream = playStream _,
        closeStream = closeStream _
      ),
      config = setStreamlinkConfig _
    )
  )

  object schema extends GenericSchema[Hello.AppEnv]
  import schema._
  val api = graphQL(RootResolver(queries, mutations))
}
