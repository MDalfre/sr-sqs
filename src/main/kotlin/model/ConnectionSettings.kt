package model

data class ConnectionSettings(
    val serverUrl: String,
    val accessKey: String,
    val secretKey: String,
    val sessionKey: String,
    val serverRegion: String
)
