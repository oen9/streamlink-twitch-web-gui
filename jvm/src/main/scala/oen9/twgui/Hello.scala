package oen9.twgui

import cats.implicits._
import zio._
import zio.blocking.Blocking
import zio.interop.catz._
import zio.logging._

import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import org.http4s.server.middleware.CORSConfig

import caliban.Http4sAdapter
import oen9.twgui.endpoints.StaticEndpoints
import oen9.twgui.gql.GQLResolver
import oen9.twgui.modules.appConfig
import oen9.twgui.modules.services.streamlinkService.StreamlinkService
import org.http4s.server.Router
import scala.concurrent.duration._

object Hello extends App {

  type AppEnv = ZEnv with appConfig.AppConfig with Logging with StreamlinkService

  type AppTask[A] = ZIO[AppEnv, Throwable, A]

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    app().provideCustomLayer {
      val appConf       = appConfig.AppConfig.live
      val logging       = slf4j.Slf4jLogger.make((_, msg) => msg)
      val streamlinkSrv = logging >>> StreamlinkService.live

      appConf ++
        logging ++
        streamlinkSrv
    }.exitCode

  def app(): ZIO[AppEnv, Throwable, Unit] =
    for {
      _ <- log.info("Hello, World!")

      cfg <- appConfig.load

      originConfig = CORSConfig(anyOrigin = true, allowCredentials = false, maxAge = 1.day.toSeconds)
      ec <- ZIO.accessM[Blocking](b => ZIO.succeed(b.get.blockingExecutor.asEC))
      catsBlocker = cats.effect.Blocker.liftExecutionContext(ec)

      gqlResolver = new GQLResolver(cfg.twitch)
      gqlInterpreter <- gqlResolver.api.interpreter

      httpApp = (
        StaticEndpoints.routes[AppEnv](cfg.assets, catsBlocker, gqlResolver.api.render)
          <+> Router("/api/graphql" -> Http4sAdapter.makeHttpService(gqlInterpreter))
      ).orNotFound

      server <- ZIO.runtime[AppEnv].flatMap { implicit rts =>
        val ec = rts.platform.executor.asEC
        BlazeServerBuilder[AppTask](ec)
          .bindHttp(cfg.http.port, cfg.http.host)
          .withHttpApp(CORS(httpApp, originConfig))
          .serve
          .compile
          .drain
      }
    } yield server
}
