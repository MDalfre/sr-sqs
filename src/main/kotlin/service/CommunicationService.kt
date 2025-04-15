package service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.Log
import model.LogTypeEnum
import model.ProcessingStatus

class CommunicationService {
    var systemLog by mutableStateOf(listOf<Log>())
    var sqsConnected by mutableStateOf(false)
    var messageCounter = 1
    var reprocessingDql = ProcessingStatus.NOT_STARTED
    var mockService = ProcessingStatus.NOT_STARTED
    var gettingQueues by mutableStateOf(ProcessingStatus.NOT_STARTED)

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
