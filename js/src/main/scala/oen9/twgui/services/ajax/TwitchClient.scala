package oen9.twgui.services.ajax

import io.circe.generic.auto._
import org.scalajs.dom.ext.Ajax
import scala.concurrent.ExecutionContext.Implicits.global
import TwitchData._

object TwitchClient {
  private[this] val JSON_TYPE = Map("Accept" -> "application/vnd.twitchtv.v5+json")

  private[this] def authHeader(clientId: String, token: String)       = Map("Client-ID" -> clientId, "Authorization" -> token)
  private[this] def krakenAuthHeader(clientId: String, token: String) = authHeader(clientId, s"OAuth $token")
  private[this] def helixAuthHeader(clientId: String, token: String)  = authHeader(clientId, s"Bearer $token")

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

  def getStreams(clientId: String, token: String) =
    Ajax
      .get(
        url = s"$helixUrl/streams",
        headers = JSON_TYPE ++ helixAuthHeader(clientId, token)
      )
      .transform(AjaxHelper.decodeAndHandleErrors[Streams])

  def getStreamsAfter(clientId: String, token: String, after: String) = {
    val queryParams = s"after=$after"
    Ajax
      .get(
        url = s"$helixUrl/streams?$queryParams",
        headers = JSON_TYPE ++ helixAuthHeader(clientId, token)
      )
      .transform(AjaxHelper.decodeAndHandleErrors[Streams])
  }

  def getStreamsFeatured(clientId: String, token: String) =
    Ajax
      .get(
        url = s"$krakenUrl/streams/featured?limit=5",
        headers = JSON_TYPE ++ krakenAuthHeader(clientId, token)
      )
      .transform(AjaxHelper.decodeAndHandleErrors[FeaturedStreams])

  def getGames(clientId: String, token: String, ids: Seq[String]) = {
    val queryParams = ids.map(id => s"id=$id").mkString("&")
    Ajax
      .get(
        url = s"$helixUrl/games?$queryParams",
        headers = JSON_TYPE ++ helixAuthHeader(clientId, token)
      )
      .transform(AjaxHelper.decodeAndHandleErrors[Games])
  }

  def getTopGamesAfter(clientId: String, token: String, after: String) = {
    val queryParams = s"after=$after"
    Ajax
      .get(
        url = s"$helixUrl/games/top?$queryParams",
        headers = JSON_TYPE ++ helixAuthHeader(clientId, token)
      )
      .transform(AjaxHelper.decodeAndHandleErrors[Games])
  }

  def getGamesTop(clientId: String, token: String) =
    Ajax
      .get(
        url = s"$helixUrl/games/top",
        headers = JSON_TYPE ++ helixAuthHeader(clientId, token)
      )
      .transform(AjaxHelper.decodeAndHandleErrors[Games])

  def getUsers(clientId: String, token: String) =
    Ajax
      .get(
        url = s"$helixUrl/users",
        headers = JSON_TYPE ++ helixAuthHeader(clientId, token)
      )
      .transform(AjaxHelper.decodeAndHandleErrors[Users])

  def getUsers(clientId: String, token: String, ids: Seq[String]) = {
    val queryParams = ids.map(id => s"id=$id").mkString("&")
    Ajax
      .get(
        url = s"$helixUrl/users?$queryParams",
        headers = JSON_TYPE ++ helixAuthHeader(clientId, token)
      )
      .transform(AjaxHelper.decodeAndHandleErrors[Users])
  }

  def getUsersFollows(clientId: String, token: String, fromId: Int) =
    Ajax
      .get(
        url = s"$helixUrl/users/follows?from_id=$fromId",
        headers = JSON_TYPE ++ helixAuthHeader(clientId, token)
      )
      .transform(AjaxHelper.decodeAndHandleErrors[UsersFollows])
}
