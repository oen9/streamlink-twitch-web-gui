package oen9.twgui.services.ajax

object TwitchData {
  case class StreamsFollowed(streams: Seq[StreamFollowed])
  case class StreamFollowed(_id: Long, game: String, broadcast_platform: String, channel: Channel, preview: Preview)
  case class Preview(small: String, medium: String, large: String)
  case class Channel(status: String, display_name: String, name: String)

  case class Streams(data: Seq[StreamData])
  case class StreamData(game_id: String, thumbnail_url: String, title: String, user_name: String, viewer_count: Int)

  case class Games(data: Seq[GameData])
  case class GameData(box_art_url: String, id: String, name: String)
}
