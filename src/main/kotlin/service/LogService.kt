package service

import model.Log
import model.LogType
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

class LogService {
    val systemLog = mutableListOf<Log>()
    var workQueue: BlockingQueue<String>? = null

    fun worker(workQueue: BlockingQueue<String>) {
        this.workQueue = workQueue
    }

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
