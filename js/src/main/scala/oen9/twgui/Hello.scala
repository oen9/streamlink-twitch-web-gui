package oen9.twgui

import org.scalajs.dom.document
import scala.scalajs.LinkingInfo

object Hello {

  def main(args: Array[String]): Unit = {
    val target = document.getElementById("main")

    if (LinkingInfo.developmentMode) {
      println("dev mode")
    }

    target.innerHTML = "Hello, World!"
  }
}
