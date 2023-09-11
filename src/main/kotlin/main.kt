import com.jessecorbett.diskord.bot.bot
import com.jessecorbett.diskord.bot.interaction.interactions
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json


val client = HttpClient {
  install(ContentNegotiation) {
    json(Json { ignoreUnknownKeys = true })
  }
}

const val w2gUrl = "https://api.w2g.tv/rooms/create.json"
const val w2gRoomUrl = "https://w2g.tv/rooms/"

var w2gApiKey: String = System.getenv("w2g-api-key")
val botToken: String = System.getenv("bot-token")


suspend fun getRoom(videoUrl: String? = null): ResponseModel {
  return runBlocking {
    client
      .post(w2gUrl) {
        headers {
          append(HttpHeaders.Accept, ContentType.Application.Json.toString())
          append(
            HttpHeaders.ContentType, ContentType.Application.Json.toString()
          )
        }
        contentType(ContentType.Application.Json)
        setBody(
          RequestModel(
            w2g_api_key = w2gApiKey,
            share = videoUrl,
            bg_color = "#002020",
            bg_opacity = null
          )
        )
      }
      .body()
  }
}

suspend fun main() {
  runBlocking {
    bot(botToken) {
      interactions {
        slashCommand("create", "Creates w2g.tv room") {
          val videoUrl: String? by stringParameter(
            "video",
            "Video url to preload",
            optional = true
          )
          callback {
            val response = getRoom(videoUrl)
            respond {
              content = if (!response.streamkey.isNullOrEmpty()) {
                "Catch that w2g url 🥴 😉 \n${w2gRoomUrl}/${response.streamkey}"
              }
              else {
                "There seems to be a problem!"
              }
            }
          }
        }
      }
    }
  }
}