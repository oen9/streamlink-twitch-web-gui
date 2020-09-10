package oen9.twgui.services

import diode.Action
import diode.data.Empty
import diode.data.Pot
import diode.data.PotAction
import oen9.twgui.services.ajax.TwitchData.StreamsFollowed

object CircuitActions {
  case class SetTwitchCred(newCred: TwitchCred) extends Action

  case class TryGetStreamsFollowed(clientId: String, token: String, potResult: Pot[StreamsFollowed] = Empty)
      extends PotAction[StreamsFollowed, TryGetStreamsFollowed] {
    def next(newResult: Pot[StreamsFollowed]) = copy(potResult = newResult)
  }
}
