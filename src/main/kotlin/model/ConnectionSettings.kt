package model

import com.amazonaws.regions.Regions

data class ConnectionSettings(
    val credentialType: CredentialType,
    val serverUrl: String,
    val accessKey: String,
    val secretKey: String,
    val sessionKey: String,
    val serverRegion: Regions
)
