package service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.Log
import model.LogTypeEnum
import model.ProcessStatusEnum

class CommunicationService {
    var systemLog by mutableStateOf(listOf<Log>())
    var messageCounter = 1
    var reprocessingDql = ProcessStatusEnum.NOT_STARTED
    var mockService = ProcessStatusEnum.NOT_STARTED

    fun logInfo(message: String) {
        systemLog = systemLog.plus(
            Log(LogTypeEnum.INFO, message)
        )
    }

    fun logWarn(message: String) {
        systemLog = systemLog.plus(
            Log(LogTypeEnum.WARN, message)
        )
    }

    fun logError(message: String?) {
        systemLog = systemLog.plus(
            Log(LogTypeEnum.ERROR, message ?: "Internal Error")
        )
    }

    fun logSuccess(message: String) {
        systemLog = systemLog.plus(
            Log(LogTypeEnum.SUCCESS, message)
        )
    }
}
