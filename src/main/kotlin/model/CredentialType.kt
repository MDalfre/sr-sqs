package model

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicSessionCredentials
import com.amazonaws.auth.BasicAWSCredentials

enum class CredentialType(val credential: (connection: ConnectionSettings) -> AWSCredentials) {
    SESSION(
        credential = { connection ->
            BasicSessionCredentials(connection.accessKey, connection.secretKey, connection.sessionKey)
        }
    ),
    BASIC(
        credential = { connection ->
            BasicAWSCredentials(connection.accessKey, connection.secretKey)
        }
    )
}
