package commons

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun Any.objectToJson(): String? {
    val mapper = jacksonObjectMapper()
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)
}

inline fun <reified T> String.jsonToObject(): T {
    val mapper = jacksonObjectMapper()
    return mapper.readValue(this, T::class.java)
}

fun String.prettyJson(): String {
    return try {
        val mapper = jacksonObjectMapper()
        val mapped = mapper.readTree(this)
        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapped)
    } catch (ex: JsonParseException) {
        this
    }
}


