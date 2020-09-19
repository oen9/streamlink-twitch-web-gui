package oen9.twgui.modules.services

import scala.sys.process._
import zio._
import zio.logging.Logging

object streamlinkService {
  type StreamlinkService = Has[StreamlinkService.Service]
  case class StreamlinkProcess(name: String, proc: Process)

  object StreamlinkService {
    trait Service {
      def play(name: String): Task[Unit]
      def close(name: String): Task[Unit]
      def getLive(): Task[Set[String]]
      def getParams(): UIO[String]
      def setParams(params: String): Task[Unit]
    }

    val live: ZLayer[Logging, Nothing, StreamlinkService] = ZLayer.fromServiceM(logging =>
      for {
        processes <- Ref.make(Set[StreamlinkProcess]())
        paramsRef <- Ref.make("""-p "vlc --fullscreen"""")
        sls = new StreamlinkServiceLive(logging, processes, paramsRef)
      } yield sls
    )
  }

  def play(name: String): ZIO[StreamlinkService, Throwable, Unit] =
    ZIO.accessM[StreamlinkService](_.get.play(name))
  def close(name: String): ZIO[StreamlinkService, Throwable, Unit] =
    ZIO.accessM[StreamlinkService](_.get.close(name))
  def getLive(): ZIO[StreamlinkService, Throwable, Set[String]] =
    ZIO.accessM[StreamlinkService](_.get.getLive())
  def getParams(): ZIO[StreamlinkService, Nothing, String] =
    ZIO.accessM[StreamlinkService](_.get.getParams())
  def setParams(params: String): ZIO[StreamlinkService, Throwable, Unit] =
    ZIO.accessM[StreamlinkService](_.get.setParams(params))
}
