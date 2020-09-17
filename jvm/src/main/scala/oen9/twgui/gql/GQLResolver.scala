package oen9.twgui.gql

import caliban.GraphQL.graphQL
import caliban.RootResolver
import caliban.schema.GenericSchema
import oen9.twgui.gql.GQLData._
import oen9.twgui.Hello
import oen9.twgui.modules.appConfig
import zio._
import zio.logging._
import oen9.twgui.modules.services.streamlinkService

class GQLResolver(appTwitchConfig: appConfig.Twitch) {
  val twitchConfig = TwitchConfig(appTwitchConfig.clientId, appTwitchConfig.token)

  def getConfig = Config(twitch = twitchConfig, streamlink = StreamlinkConfig("foo"))

  def playStream(args: StreamNameArgs): ZIO[Hello.AppEnv, Throwable, Boolean] =
    for {
      _ <- log.info(s"playStream: ${args.name}")
      _ <- streamlinkService.play(args.name)
    } yield true

  val queries   = Queries(config = getConfig, video = Video(live = Seq()))
  val mutations = Mutations(streamlink = StreamlinkMut(
    video = VideoMut(
      playStream = playStream _,
      closeStream = _ => ZIO.succeed(false)
    ),
    config = _ => ZIO.succeed(false)
  ))

  object schema extends GenericSchema[Hello.AppEnv]
  import schema._
  val api = graphQL(RootResolver(queries, mutations))
}
