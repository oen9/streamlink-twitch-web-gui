package oen9.twgui

import oen9.twgui.bridges.reactrouter.HashRouter
import oen9.twgui.modules.MainRouter
import org.scalajs.dom.document
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.LinkingInfo
import slinky.web.ReactDOM

object Hello {

  @JSImport("bootstrap", JSImport.Default)
  @js.native
  object Bootstrap extends js.Object

  def main(args: Array[String]): Unit = {
    val target = document.getElementById("main")

    Bootstrap

    if (LinkingInfo.developmentMode) {
      println("dev mode")
    }

    ReactDOM.render(HashRouter(MainRouter()), target)
  }
}
