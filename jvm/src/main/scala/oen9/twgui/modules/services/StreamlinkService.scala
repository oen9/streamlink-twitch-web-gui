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
      sls = new StreamlinkServiceLive(ref)
    } yield sls)
  }

  def play(name: String): ZIO[StreamlinkService, Throwable, Unit] =
    ZIO.accessM[StreamlinkService](_.get.play(name))
}
