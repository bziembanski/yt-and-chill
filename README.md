[![Ktor](https://img.shields.io/badge/Ktor-2.3.4-blue.svg)](https://central.sonatype.com/artifact/io.ktor/ktor/2.3.4)
[![Maven Central](https://img.shields.io/maven-central/v/com.jessecorbett/diskord-bot.svg?label=Diskord)](https://gitlab.com/diskord/diskord)

# yt-and-chill
### Simple discord bot for managing w2g.tv rooms.

Written in kotlin using `Diskord` client. Implemented using api described in [this](https://community.w2g.tv/t/watch2gether-api-documentation/133767) community post.

#

Commands:<br>
- `create video:url` - Used to create new room. Optional parameter `video` is responsible for passing an url to a video that will be preloaded in the room.

#

Features:<br>
✅ Creating a room<br>
✅ Creating a room with preloaded video - passing url in `create` command<br>
✅ Sharing an item - changing currently playing video<br>
🔳 Adding items to currently playing playlist<br>
