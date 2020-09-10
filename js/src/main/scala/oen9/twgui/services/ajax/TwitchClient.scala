package oen9.twgui.services.ajax

import io.circe.generic.auto._
import org.scalajs.dom.ext.Ajax
import scala.concurrent.ExecutionContext.Implicits.global
import TwitchData._

object TwitchClient {
  private[this] val JSON_TYPE = Map("Accept" -> "application/vnd.twitchtv.v5+json")

  private[this] def authHeader(clientId: String, token: String)       = Map("Client-ID" -> clientId, "Authorization" -> token)
  private[this] def krakenAuthHeader(clientId: String, token: String) = authHeader(clientId, s"OAuth $token")
  private[this] def helixAuthHeader(clientId: String, token: String)  = authHeader(clientId, s"Barrer $token")

  private[this] val baseUrl   = "https://api.twitch.tv"
  private[this] val krakenUrl = s"$baseUrl/kraken"
  private[this] val helixUrl  = s"$baseUrl/helix"

  def getStreamsFollowed(clientId: String, token: String) =
    Ajax
      .get(
        url = s"$krakenUrl/streams/followed",
        headers = JSON_TYPE ++ krakenAuthHeader(clientId, token)
      )
      .transform(AjaxHelper.decodeAndHandleErrors[StreamsFollowed])
}
