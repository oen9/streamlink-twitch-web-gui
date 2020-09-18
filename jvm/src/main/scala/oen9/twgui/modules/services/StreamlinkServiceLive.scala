package oen9.twgui.modules.services

import oen9.twgui.modules.services.streamlinkService.StreamlinkProcess
import oen9.twgui.modules.services.streamlinkService.StreamlinkService.Service
import scala.sys.process._
import zio._
import zio.logging.Logger

class StreamlinkServiceLive(logger: Logger[String], processes: Ref[Set[StreamlinkProcess]]) extends Service {
  override def play(name: String): Task[Unit] =
    for {
      _              <- logger.info(s"play: $name")
      liveProccesses <- processes.get
      _              <- liveProccesses.find(_.name == name).fold(unit)(_ => logAndFail(s"$name is already live"))
      newProc        <- ZIO.effect((s"streamlink twitch.tv/$name best").run())
      _              <- processes.update(_ + StreamlinkProcess(name, newProc))
    } yield ()

  override def close(name: String): Task[Unit] =
    for {
      _ <- logger.info(s"close: $name")
      procs <- processes.update { ps =>
        val toClose = ps.find(_.name == name)
        toClose.foreach(_.proc.destroy())
        toClose.fold(ps)(ps - _)
      }
    } yield ()

  override def getLive(): Task[Set[String]] =
    for {
      procs <- processes.updateAndGet(_.filter(_.proc.isAlive()))
      liveNames = procs.map(_.name)
      _ <- logger.debug(s"getLive: ${liveNames.mkString(", ")}")
    } yield liveNames

  private def unit: Task[Unit] = ZIO.unit
  private def logAndFail(msg: String): Task[Unit] =
    for {
      _ <- logger.error(msg)
      _ <- ZIO.fail(new Throwable(msg))
    } yield ()
}
