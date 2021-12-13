package service

import com.amazonaws.regions.Regions
import model.ConnectionSettings
import model.CredentialType
import model.Queue
import java.io.File
import java.io.FileInputStream
import java.util.Properties

const val CREDENTIAL_TYPE = "selectedCredential"
const val SERVER_URL = "serverUrl"
const val ACCESS_KEY = "accessKey"
const val SECRET_KEY = "secretKey"
const val SESSION_KEY = "sessionKey"
const val REGION = "selectedRegion"

class FileHandleService {

    fun createFile(queueList: List<Queue>) {
        val fileName = "init.sh"
        File(fileName).printWriter().use { out ->
            out.println("#!/bin/bash")
            out.println("set -x")
            out.println("")
            queueList.forEach {
                out.println("awslocal sqs create-queue --queue-name ${it.name}")
            }
            out.println("")
            out.println("set +x")
        }
    }

    fun createConfigFile(connectionSettings: ConnectionSettings) {
        val fileName = "config.cfg"
        File(fileName).printWriter().use { out ->
            out.println("$CREDENTIAL_TYPE=${connectionSettings.credentialType}")
            out.println("$SERVER_URL=${connectionSettings.serverUrl}")
            out.println("$ACCESS_KEY=${connectionSettings.accessKey}")
            out.println("$SECRET_KEY=${connectionSettings.secretKey}")
            out.println("$SESSION_KEY=${connectionSettings.sessionKey}")
            out.println("$REGION=${connectionSettings.serverRegion}")
        }
    }

    fun readConfigFile(): ConnectionSettings {
        val fileName = "config.cfg"
        if (File(fileName).exists()) {
            val prop = Properties()
            FileInputStream(fileName).use {
                prop.load(it)
                prop.getProperty(CREDENTIAL_TYPE)
                prop.getProperty(SERVER_URL)
                prop.getProperty(ACCESS_KEY)
                prop.getProperty(SECRET_KEY)
                prop.getProperty(SESSION_KEY)
                prop.getProperty(REGION)
                val credentialType = prop.entries.find { properties ->
                    properties.key == CREDENTIAL_TYPE
                }?.value.toString()
                val serverUrl = prop.entries.find { properties ->
                    properties.key == SERVER_URL
                }?.value.toString()
                val accessKey = prop.entries.find { properties ->
                    properties.key == ACCESS_KEY
                }?.value.toString()
                val secretKey = prop.entries.find { properties ->
                    properties.key == SECRET_KEY
                }?.value.toString()
                val sessionKey = prop.entries.find { properties ->
                    properties.key == SESSION_KEY
                }?.value.toString()
                val region = prop.entries.find { properties ->
                    properties.key == REGION
                }?.value.toString()

                return ConnectionSettings(
                    credentialType = CredentialType.valueOf(credentialType),
                    serverUrl = serverUrl,
                    accessKey = accessKey,
                    secretKey = secretKey,
                    sessionKey = sessionKey,
                    serverRegion = Regions.valueOf(region)
                )
            }
        }

        return ConnectionSettings(
            credentialType = CredentialType.BASIC,
            serverUrl = "http://localhost:4566",
            accessKey = "docker",
            secretKey = "docker",
            sessionKey = "docker",
            serverRegion = Regions.US_EAST_1
        )
    }
}
