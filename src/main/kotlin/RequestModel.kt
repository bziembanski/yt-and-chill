import kotlinx.serialization.Serializable

@Serializable
data class RequestModel(
  val w2g_api_key:String,
  val share: String?,
  val bg_color: String?,
  val bg_opacity: String?
)
