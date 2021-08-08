package service

class LogService {
    val systemLog = mutableListOf<String>()

    fun log(message: String) {
        systemLog.add(message)
    }
}