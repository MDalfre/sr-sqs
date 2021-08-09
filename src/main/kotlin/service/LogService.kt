package service

import model.Log
import model.LogType

class LogService {
    val systemLog = mutableListOf<Log>()

    fun info(message: String) {
        systemLog.add(
            Log(LogType.INFO, message)
        )
    }

    fun warn(message: String) {
        systemLog.add(
            Log(LogType.WARN, message)
        )
    }

    fun error(message: String?) {
        systemLog.add(
            Log(LogType.ERROR, message ?: "Internal Error")
        )
    }
}
