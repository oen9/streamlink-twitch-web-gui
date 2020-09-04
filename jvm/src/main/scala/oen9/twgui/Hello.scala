package oen9.twgui

import zio._
import zio.console._

object Hello extends App {

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = app().exitCode

  def app() =
    for {
      _ <- putStrLn("Hello, World!")
    } yield ()
}
