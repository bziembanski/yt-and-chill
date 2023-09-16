import com.jessecorbett.diskord.bot.bot
import com.jessecorbett.diskord.bot.interaction.interactions
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlin.time.Duration.Companion.hours
import kotlinx.coroutines.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

val client = HttpClient { install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) } }

const val w2gUrl = "https://api.w2g.tv/rooms/"
const val w2gUrlCreateUrl = "create.json"
const val w2gRoomUrl = "https://w2g.tv/rooms/"
const val w2gRoomUpdateVideoEndpoint = "sync_update"

var w2gApiKey: String = System.getenv("w2g-api-key")
val botToken: String = System.getenv("bot-token")

val rooms = mutableListOf<String>()

@kotlin.time.ExperimentalTime
suspend fun addRoom(roomId: String) {
  rooms.add(roomId)
  delay(24.hours)
  println("removing room $roomId")
  rooms.remove(roomId)
}

suspend fun getRoom(videoUrl: String? = null): ResponseModel {
  return client
      .post("$w2gUrl$w2gUrlCreateUrl") {
        headers {
          append(HttpHeaders.Accept, ContentType.Application.Json.toString())
          append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
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

suspend fun changeVideo(videoUrl: String, roomId: String) {
  client.post("$w2gUrl$roomId/$w2gRoomUpdateVideoEndpoint") {
    headers {
      append(HttpHeaders.Accept, ContentType.Application.Json.toString())
      append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    }
    contentType(ContentType.Application.Json)
    setBody(UpdateVideoRequestModel(w2g_api_key = w2gApiKey, item_url = videoUrl))
  }
}

@kotlin.time.ExperimentalTime
suspend fun main() {
  runBlocking {
    bot(botToken) {
      interactions {
        slashCommand("create", "Creates w2g.tv room") {
          val videoUrl: String? by stringParameter("video", "Video url to preload", optional = true)
          callback {
            val response = getRoom(videoUrl)
            if (!response.streamkey.isNullOrEmpty()) {
              launch { addRoom(response.streamkey) }
            }
            respond {
              content =
                  if (!response.streamkey.isNullOrEmpty()) {
                    "Catch that w2g url 🥴 😉 \n${w2gRoomUrl}${response.streamkey}"
                  } else {
                    "There seems to be a problem!"
                  }
            }
          }
        }
        slashCommand("rooms", "Displays list of rooms") {
          callback {
            respond {
              var roomsString = ""
              if (rooms.isEmpty()) {
                roomsString = "No active rooms! 🤔 😔"
              } else {
                roomsString += "List of rooms in format: `index: roomdId`\n"
                rooms.forEachIndexed { index, room ->
                  if (index != 0) {
                    roomsString += "\n"
                  }
                  roomsString += "- `$index: $room`"
                }
              }
              content = roomsString
            }
          }
        }
        slashCommand(
            "update_video",
            "Changes current video in given room. Specify room using roomId or roomIndex from list of rooms."
        ) {
          val videoUrl: String? by
              stringParameter(
                  "video",
                  "Video url to preload",
                  optional = false,
              )
          val roomId: String? by
              stringParameter(
                  "room_id",
                  "Id of a room to update",
                  optional = true,
              )
          val roomIndex: Float? by
              floatParameter(
                  "room_index",
                  "Index of a room to update",
                  optional = true,
              )
          callback {
            if (rooms.isEmpty()) {
              respond { content = "No active rooms! 🤔 😔" }
            } else if (videoUrl != null && (roomId != null || roomIndex != null)) {
              val id = roomId ?: rooms[roomIndex!!.toInt()]
              changeVideo(videoUrl!!, id)
              respond { content = "Congratulations, you've updated room $id" }
            } else {
              respond {
                content =
                    "You have to specify `room_id` or `room_index`.\nGet them from `\\rooms` command"
              }
            }
          }
        }
      }
    }
  }
}