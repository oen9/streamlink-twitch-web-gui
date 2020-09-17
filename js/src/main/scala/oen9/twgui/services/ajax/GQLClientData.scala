package oen9.twgui.services.ajax

import caliban.client.FieldBuilder._
import caliban.client.SelectionBuilder._
import caliban.client._
import caliban.client.Operations._

object GQLClientData {

  type Config
  object Config {
    def twitch[A](innerSelection: SelectionBuilder[TwitchConfig, A]): SelectionBuilder[Config, A] =
      Field("twitch", Obj(innerSelection))
    def streamlink[A](innerSelection: SelectionBuilder[StreamlinkConfig, A]): SelectionBuilder[Config, A] =
      Field("streamlink", Obj(innerSelection))
  }

  type LiveVideo
  object LiveVideo {
    def name: SelectionBuilder[LiveVideo, String] = Field("name", Scalar())
  }

  type StreamlinkConfig
  object StreamlinkConfig {
    def params: SelectionBuilder[StreamlinkConfig, String] = Field("params", Scalar())
  }

  type StreamlinkMut
  object StreamlinkMut {
    def video[A](innerSelection: SelectionBuilder[VideoMut, A]): SelectionBuilder[StreamlinkMut, A] =
      Field("video", Obj(innerSelection))
    def config(params: String): SelectionBuilder[StreamlinkMut, Option[Boolean]] =
      Field("config", OptionOf(Scalar()), arguments = List(Argument("params", params)))
  }

  type TwitchConfig
  object TwitchConfig {
    def clientId: SelectionBuilder[TwitchConfig, String] = Field("clientId", Scalar())
    def token: SelectionBuilder[TwitchConfig, String]    = Field("token", Scalar())
  }

  type Video
  object Video {
    def live[A](innerSelection: SelectionBuilder[LiveVideo, A]): SelectionBuilder[Video, Option[List[A]]] =
      Field("live", OptionOf(ListOf(Obj(innerSelection))))
  }

  type VideoMut
  object VideoMut {
    def playStream(name: String): SelectionBuilder[VideoMut, Option[Boolean]] =
      Field("playStream", OptionOf(Scalar()), arguments = List(Argument("name", name)))
    def closeStream(name: String): SelectionBuilder[VideoMut, Option[Boolean]] =
      Field("closeStream", OptionOf(Scalar()), arguments = List(Argument("name", name)))
  }

  type Queries = RootQuery
  object Queries {
    def config[A](innerSelection: SelectionBuilder[Config, A]): SelectionBuilder[RootQuery, A] =
      Field("config", Obj(innerSelection))
    def video[A](innerSelection: SelectionBuilder[Video, A]): SelectionBuilder[RootQuery, A] =
      Field("video", Obj(innerSelection))
  }

  type Mutations = RootMutation
  object Mutations {
    def streamlink[A](innerSelection: SelectionBuilder[StreamlinkMut, A]): SelectionBuilder[RootMutation, A] =
      Field("streamlink", Obj(innerSelection))
  }

}

