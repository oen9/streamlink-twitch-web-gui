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
  }

  type TwitchConfig
  object TwitchConfig {
    def clientId: SelectionBuilder[TwitchConfig, String] = Field("clientId", Scalar())
    def token: SelectionBuilder[TwitchConfig, String]    = Field("token", Scalar())
  }

  type Queries = RootQuery
  object Queries {
    def config[A](innerSelection: SelectionBuilder[Config, A]): SelectionBuilder[RootQuery, A] =
      Field("config", Obj(innerSelection))
  }

}

