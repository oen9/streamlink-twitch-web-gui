package oen9.twgui

import cats.implicits._
import zio._
import zio.logging._
import zio.interop.catz._

import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import org.http4s.server.middleware.CORSConfig
import org.http4s.implicits._

import scala.concurrent.duration._
import oen9.twgui.endpoints.StaticEndpoints
import zio.blocking.Blocking

object Hello extends App {

  type AppEnv = ZEnv with Logging

  type AppTask[A] = ZIO[AppEnv, Throwable, A]

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    app().provideCustomLayer {
      val logging = slf4j.Slf4jLogger.make((_, msg) => msg)
      logging
    }.exitCode

  def app(): ZIO[AppEnv, Throwable, Unit] =
    for {
      _ <- log.info("Hello, WOrld!")

      originConfig = CORSConfig(anyOrigin = true, allowCredentials = false, maxAge = 1.day.toSeconds)
      ec <- ZIO.accessM[Blocking](b => ZIO.succeed(b.get.blockingExecutor.asEC))
      catsBlocker = cats.effect.Blocker.liftExecutionContext(ec)

      httpApp = (
        StaticEndpoints.routes[AppEnv]("assets", catsBlocker) // TODO assets to cfg
      )
      .orNotFound

      server <- ZIO.runtime[AppEnv].flatMap { implicit rts =>
        val ec = rts.platform.executor.asEC
        BlazeServerBuilder[AppTask](ec)
          .bindHttp(8080, "localhost") // TODO port and host to cfg
          .withHttpApp(CORS(httpApp, originConfig))
          .serve
          .compile
          .drain
      }
    } yield server
}
