package service

import model.Log
import model.LogTypeEnum
import model.ProcessStatusEnum

class CommunicationService {
    val systemLog = mutableListOf<Log>()
    var messageCounter = 1
    var reprocessingDql = ProcessStatusEnum.NOT_STARTED
    var mockService = ProcessStatusEnum.NOT_STARTED

    fun logInfo(message: String) {
        systemLog.add(
            Log(LogTypeEnum.INFO, message)
        )
    }

    fun logWarn(message: String) {
        systemLog.add(
            Log(LogTypeEnum.WARN, message)
        )
    }

    fun logError(message: String?) {
        systemLog.add(
            Log(LogTypeEnum.ERROR, message ?: "Internal Error")
        )
    }

    fun logSuccess(message: String) {
        systemLog.add(
            Log(LogTypeEnum.SUCCESS, message)
        )
    }
}
