package oen9.twgui.services.ajax

import caliban.client.CalibanClientError
import caliban.client.Operations
import caliban.client.SelectionBuilder
import GQLClientData._
import oen9.twgui.services.TwitchCred
import oen9.twgui.services.{StreamlinkConfig => StreamlinkCfg}
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
  def getStreamlinkConfigQuery = Queries.config {
    Config.streamlink {
      StreamlinkConfig.params
    }.map(StreamlinkCfg)
  }
  def getLiveVideosQuery = Queries.video {
    Video.live {
      LiveVideo.name
    }
  }

  def playStreamMutation(name: String) = Mutations.streamlink {
    StreamlinkMut.video {
      VideoMut.playStream(name)
    }
  }
  def closeStreamMutation(name: String) = Mutations.streamlink {
    StreamlinkMut.video {
      VideoMut.closeStream(name)
    }
  }
  def streamlinkConfigMutation(cfg: StreamlinkCfg) = Mutations.streamlink {
    StreamlinkMut.config(cfg.params)
  }

  def getTwitchConfig                          = runQuery(getTwitchConfigQuery)
  def getStreamlinkConfig                      = runQuery(getStreamlinkConfigQuery)
  def getLiveVideos                            = runQuery(getLiveVideosQuery).map(_.fold(Set[String]())(_.toSet))
  def playStream(name: String)                 = runMutation(playStreamMutation(name))
  def closeStream(name: String)                = runMutation(closeStreamMutation(name))
  def saveStreamlinkConfig(cfg: StreamlinkCfg) = runMutation(streamlinkConfigMutation(cfg))

  private def runQuery[A](query: SelectionBuilder[Operations.RootQuery, A])       = runRequest(query.toRequest(uri))
  private def runMutation[A](query: SelectionBuilder[Operations.RootMutation, A]) = runRequest(query.toRequest(uri))
  private def runRequest[A](request: Request[Either[CalibanClientError, A], Nothing]) =
    sttpBackend
      .send(request)
      .map(_.body)
      .flatMap(handleError)

  private def handleError[A](value: Either[CalibanClientError, A]): Future[A] = value match {
    case Left(error) => Future.failed(error)
    case Right(succ) => Future.successful(succ)
  }
}
