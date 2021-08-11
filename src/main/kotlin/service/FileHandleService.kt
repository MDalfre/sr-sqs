package service

import model.Queue
import java.io.File

class FileHandleService {

    fun createFile(queueList: List<Queue>) {
        val fileName = "init.sh"
        File(fileName).printWriter().use { out ->
            out.println("#!/bin/bash")
            out.println("set -x")
            out.println("")
            queueList.forEach {
                out.println("awslocal sqs create-queue --queue-name ${it.name}")
            }
            out.println("")
            out.println("set +x")
        }
    }
}