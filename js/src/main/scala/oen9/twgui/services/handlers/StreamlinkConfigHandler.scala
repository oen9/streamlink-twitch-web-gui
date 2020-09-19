package oen9.twgui.services.handlers

import diode.Effect
import diode.{ActionHandler, ModelRW}
import oen9.twgui.services.ajax.GQLClient
import oen9.twgui.services.CircuitActions.GetStreamlinkConfig
import oen9.twgui.services.CircuitActions.GotStreamlinkConfig
import oen9.twgui.services.StreamlinkConfig
import scala.concurrent.ExecutionContext.Implicits.global
import oen9.twgui.services.CircuitActions.SetStreamlinkConfig

class StreamlinkConfigHandler[M](modelRW: ModelRW[M, StreamlinkConfig]) extends ActionHandler(modelRW) {
  override def handle = {
    case SetStreamlinkConfig(newCfg) =>
      val saveData = Effect(GQLClient.saveStreamlinkConfig(newCfg).map(_ => GetStreamlinkConfig))
      effectOnly(saveData)

    case GetStreamlinkConfig =>
      val fetchData = Effect(GQLClient.getStreamlinkConfig.map(GotStreamlinkConfig))
      effectOnly(fetchData)

    case GotStreamlinkConfig(newCfg) =>
      updated(newCfg)
  }
}
