package commons

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun Any.objectToJson(): String? {
    val mapper = jacksonObjectMapper()
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)
}

inline fun <reified T> String.jsonToObject(): T {
    val mapper = jacksonObjectMapper()
    return mapper.readValue(this, T::class.java)
}
