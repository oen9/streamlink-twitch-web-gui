package oen9.twgui.gql

import oen9.twgui.Hello
import zio._

object GQLData {
  case class TwitchConfig(clientId: String, token: String)
  case class StreamlinkConfig(params: String)
  case class Config(twitch: TwitchConfig, streamlink: StreamlinkConfig)
  case class Video(
    live: ZIO[Hello.AppEnv, Throwable, Set[LiveVideo]]
  )
  case class LiveVideo(name: String)

  case class StreamlinkMut(
    video: VideoMut,
    config: StreamlinkConfig => ZIO[Hello.AppEnv, Throwable, Boolean]
  )
  case class VideoMut(
    playStream: StreamNameArgs => ZIO[Hello.AppEnv, Throwable, Boolean],
    closeStream: StreamNameArgs => ZIO[Hello.AppEnv, Throwable, Boolean]
  )
  case class StreamNameArgs(name: String)

  case class Queries(config: Config, video: Video)
  case class Mutations(streamlink: StreamlinkMut)
}
