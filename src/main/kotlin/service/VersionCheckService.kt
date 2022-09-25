package service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.FileNotFoundException
import java.net.URL

class VersionCheckService {

    fun checkVersion(version: String): Boolean {
        return try {
            val request = URL("https://api.github.com/repos/MDalfre/sr-sqs/releases").readText()
            val mapper = jacksonObjectMapper()
            val tags = mapper.readTree(request).findValues("tag_name")
            val formattedTags = mutableListOf<String>()
            tags.forEach { tag ->
                formattedTags.add(tag.asText().takeWhile { it.toString() != "-" })
            }
            version.uppercase() == formattedTags.first().uppercase()
        } catch (ex: FileNotFoundException) {
            println("offline? ${ex.message}")
            true
        }
    }
}
