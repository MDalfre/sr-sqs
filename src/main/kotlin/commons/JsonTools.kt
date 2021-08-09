package commons

import com.fasterxml.jackson.databind.ObjectMapper

fun Any.objectToJson(): String? {
    val mapper = ObjectMapper()
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)
}
