package oen9.twgui.modules.services

import zio._
import scala.sys.process._

object streamlinkService {
  type StreamlinkService = Has[StreamlinkService.Service]
  case class StreamlinkProcess(name: String, proc: Process)

  object StreamlinkService {
    trait Service {
      def play(name: String): Task[Unit]
    }

    val live: Layer[Nothing, StreamlinkService] = ZLayer.fromEffect(for {
      ref <- Ref.make(Seq[StreamlinkProcess]())
      sls = new Service {
        override def play(name: String): Task[Unit] =
          for {
            procs   <- ref.get
            _       <- ZIO.effect(procs.foreach(_.proc.destroy()))
            newProc <- ZIO.effect((s"streamlink twitch.tv/$name best").run())
            _       <- ref.update(_ :+ StreamlinkProcess(name, newProc))
          } yield ()
      }
    } yield sls)
  }

  def play(name: String): ZIO[StreamlinkService, Throwable, Unit] =
    ZIO.accessM[StreamlinkService](_.get.play(name))
}
