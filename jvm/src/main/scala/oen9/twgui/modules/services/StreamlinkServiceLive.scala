package oen9.twgui.modules.services

import oen9.twgui.modules.services.streamlinkService.StreamlinkProcess
import oen9.twgui.modules.services.streamlinkService.StreamlinkService.Service
import scala.sys.process._
import zio._

class StreamlinkServiceLive(processes: Ref[Seq[StreamlinkProcess]]) extends Service {
  override def play(name: String): Task[Unit] =
    for {
      procs   <- processes.get
      _       <- ZIO.effect(procs.foreach(_.proc.destroy()))
      newProc <- ZIO.effect((s"streamlink twitch.tv/$name best").run())
      _       <- processes.update(_ :+ StreamlinkProcess(name, newProc))
    } yield ()
}
