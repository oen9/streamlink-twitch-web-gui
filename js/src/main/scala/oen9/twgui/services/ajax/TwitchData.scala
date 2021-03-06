package oen9.twgui.services.ajax

object TwitchData {
  case class StreamsFollowed(streams: Seq[StreamFollowed] = Seq())
  case class StreamFollowed(_id: Long, game: String, broadcast_platform: String, channel: Channel, preview: Preview)
  case class Preview(small: String, medium: String, large: String, template: String)
  case class Channel(status: String, display_name: String, name: String)

  case class Streams(data: Seq[StreamData] = Seq(), pagination: Pagination = Pagination())
  case class StreamData(game_id: String, thumbnail_url: String, title: String, user_name: String, viewer_count: Int)

  case class Games(data: Seq[GameData] = Seq(), pagination: Pagination = Pagination())
  case class GameData(box_art_url: String, id: String, name: String)
  case class Pagination(cursor: Option[String] = None)

  case class FeaturedStreams(featured: Seq[FeaturedS])
  case class FeaturedS(stream: FeaturedStream)
  case class FeaturedStream(preview: Preview, channel: FeaturedChannel, viewers: Int)
  case class FeaturedChannel(_id: Long, status: String, display_name: String, game: String, followers: Int)

  case class Users(data: Seq[UserData])
  case class UserData(
    id: Int = 0,
    login: String = "",
    display_name: String = "",
    description: String = "",
    profile_image_url: String = "",
    offline_image_url: String = ""
  )

  case class UsersFollows(total: Int, data: Seq[UserFollow], pagination: Pagination = Pagination())
  case class UserFollow(from_id: String, from_name: String, to_id: String, to_name: String)
}
