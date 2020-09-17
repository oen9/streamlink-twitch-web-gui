package oen9.twgui.modules.services

import zio._
import scala.sys.process._
import zio.logging.Logging

object streamlinkService {
  type StreamlinkService = Has[StreamlinkService.Service]
  case class StreamlinkProcess(name: String, proc: Process)

  object StreamlinkService {
    trait Service {
      def play(name: String): Task[Unit]
      def close(name: String): Task[Unit]
      def getLive(): Task[Set[String]]
    }

    val live: ZLayer[Logging, Nothing, StreamlinkService] = ZLayer.fromServiceM(logging =>
      for {
        ref <- Ref.make(Set[StreamlinkProcess]())
        sls = new StreamlinkServiceLive(logging, ref)
      } yield sls
    )
  }

  def play(name: String): ZIO[StreamlinkService, Throwable, Unit] =
    ZIO.accessM[StreamlinkService](_.get.play(name))
  def close(name: String): ZIO[StreamlinkService, Throwable, Unit] =
    ZIO.accessM[StreamlinkService](_.get.close(name))
  def getLive(): ZIO[StreamlinkService, Throwable, Set[String]] =
    ZIO.accessM[StreamlinkService](_.get.getLive())
}
