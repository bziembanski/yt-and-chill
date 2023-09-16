import kotlinx.serialization.Serializable

@Serializable
data class RequestModel(
  val w2g_api_key:String,
  val share: String?,
  val bg_color: String?,
  val bg_opacity: String?
)

@Serializable
data class UpdateVideoRequestModel(
  val w2g_api_key:String,
  val item_url: String,
)

@Serializable
data class AddToPlaylistRequestModel(
  val w2g_api_key:String,
  val add_items: List<PlaylistItemModel>,
)

@Serializable
data class PlaylistItemModel(
  val url:String,
  val title: String?,
)
