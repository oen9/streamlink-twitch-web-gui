package oen9.twgui.modules

import pureconfig.ConfigSource
import pureconfig.generic.auto._
import zio._

object appConfig {
  case class Http(port: Int, host: String)
  case class Twitch(clientId: String, token: String)
  case class AppConfigData(http: Http, twitch: Twitch, assets: String)

  type AppConfig = Has[AppConfig.Service]

  object AppConfig {
    trait Service {
      def load: Task[AppConfigData]
    }

    val live: Layer[Nothing, AppConfig] = ZLayer.succeed(new Service {
      def load: ZIO[Any, Throwable, AppConfigData] = Task.effect(ConfigSource.default.loadOrThrow[AppConfigData])
    })
  }

  def load: ZIO[AppConfig, Throwable, AppConfigData] =
    ZIO.accessM[AppConfig](_.get.load)
}
