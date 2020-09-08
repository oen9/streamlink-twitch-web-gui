package oen9.twgui.services

import org.scalajs.dom.window.localStorage

object Storage {
  val TWITCH_CLIENT_ID = "twitch-client-id"
  val TWITCH_TOKEN     = "twitch-token"

  def saveTwitchCred(cred: TwitchCred): Unit = {
    localStorage.setItem(TWITCH_CLIENT_ID, cred.clientId)
    localStorage.setItem(TWITCH_TOKEN, cred.token)
    println("storage: savedd " + cred.toString)
  }

  def readTwitchCred(): TwitchCred = {
    val clientId = localStorage.getItem(TWITCH_CLIENT_ID)
    val token    = localStorage.getItem(TWITCH_TOKEN)

    TwitchCred(clientId = clientId, token = token)
  }
}
