package oen9.twgui.services.ajax

import caliban.client.CalibanClientError
import caliban.client.Operations
import caliban.client.SelectionBuilder
import GQLClientData._
import oen9.twgui.services.TwitchCred
import org.scalajs.dom
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.LinkingInfo
import sttp.client._
import sttp.client.FetchBackend

object GQLClient {
  lazy val sttpBackend = FetchBackend()
  val baseUrl          = if (LinkingInfo.developmentMode) "http://localhost:8080" else dom.window.location.origin
  val uri              = uri"$baseUrl/api/graphql"

  def getTwitchConfigQuery = Queries.config {
    Config.twitch {
      TwitchConfig.clientId ~ TwitchConfig.token
    }.mapN(TwitchCred)
  }

  def getTwitchConfig = runRequest(getTwitchConfigQuery)

  private def runRequest[A](query: SelectionBuilder[Operations.RootQuery, A]) =
    sttpBackend
      .send(query.toRequest(uri))
      .map(_.body)
      .flatMap(handleError)

  private def handleError[A](value: Either[CalibanClientError, A]): Future[A] = value match {
    case Left(error) => Future.failed(error)
    case Right(succ) => Future.successful(succ)
  }
}
