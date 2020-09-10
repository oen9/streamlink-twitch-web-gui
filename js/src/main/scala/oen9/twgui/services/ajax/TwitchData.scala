package oen9.twgui.services.ajax

object TwitchData {
  case class StreamsFollowed(streams: Seq[StreamFollowed])
  case class StreamFollowed(_id: Long, game: String, broadcast_platform: String, channel: Channel, preview: Preview)
  case class Preview(small: String, medium: String, large: String)
  case class Channel(status: String, display_name: String, name: String)
}
