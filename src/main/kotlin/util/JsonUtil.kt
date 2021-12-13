package util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object JsonUtil {

    fun Any.objectToJson(): String {
        val objectMapper = jacksonObjectMapper()
//        objectMapper.registerModule(JavaTimeModule())
        return objectMapper.writeValueAsString(this)
    }

    inline fun <reified T> String.jsonToObject(): T {
        val objectMapper = jacksonObjectMapper()
//        objectMapper.registerModule(JavaTimeModule())
        return objectMapper.readValue<T>(this, T::class.java)
    }
}
