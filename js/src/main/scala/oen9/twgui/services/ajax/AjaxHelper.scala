package oen9.twgui.services.ajax

import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.parser.decode
import org.scalajs.dom.ext.AjaxException
import org.scalajs.dom.raw.XMLHttpRequest
import scala.util.Failure
import scala.util.Success
import scala.util.Try

object AjaxHelper {
  def decodeAndHandleErrors[A: Decoder](t: Try[XMLHttpRequest]): Try[A] = t match {
    case Success(req) => decode[A](req.responseText).toTry
    case Failure(e)   => Failure(onFailure(e))
  }

  private[this] def onFailure: Throwable => Throwable = t => {
    t.printStackTrace()
    t match {
      case ex: AjaxException =>
        val msgEx: Exception =
          decode[GenericMsgException](ex.xhr.responseText).getOrElse(AjaxErrorException(ex.xhr.responseText))
        ex.xhr.status match {
          case 401 | 409 => msgEx
          case _         => AjaxErrorException(s"Connection error.")
        }
      case unknown => UnknownErrorException
    }
  }

  case object UnknownErrorException           extends Exception("unknown error")
  case class AjaxErrorException(s: String)    extends Exception(s)
  case class GenericMsgException(msg: String) extends Exception(msg)
}
