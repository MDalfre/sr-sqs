package service

import com.amazonaws.SdkClientException
import com.amazonaws.services.sqs.model.AmazonSQSException
import com.amazonaws.services.sqs.model.CreateQueueRequest
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.QueueAttributeName
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.amazonaws.services.sqs.model.SendMessageRequest
import model.Queue
import java.util.concurrent.BlockingDeque


const val WAIT_TIME_SECONDS = 1
const val MAX_NUMBER_OF_MESSAGES = 10

class GenericSqsService(
    private val connectionService: ConnectionService,
    private val logService: LogService
) {
    fun send(queueUrl: String, message: String, delay: Int, log: Boolean = true) {
        if (log) {
            logService.info("Sending messages ...")
        }
        message.split("//").forEach {
            try {
                val sendMessageRequest = SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(it.trim())
                    .withDelaySeconds(delay)

                connectionService.sqs.sendMessage(sendMessageRequest)
            } catch (ex: AmazonSQSException) {
                logService.error(ex.message)
                throw ex
            }
        }
    }

    fun getQueues(): MutableList<Queue> {
        val queueResponse: MutableList<Queue> = mutableListOf()
        try {
            logService.info("Retrieving queues")
            connectionService.sqs.listQueues().queueUrls.forEach {
                queueResponse.add(
                    Queue(
                        name = it.substringAfterLast("/"),
                        url = it
                    )
                )
            }
            return queueResponse
        } catch (ex: SdkClientException) {
            println(ex.message)
            logService.error(ex.message)
            throw ex
        }
    }

    fun getQueueInfo(queueUrl: String) {
        val attributesRequest = listOf(QueueAttributeName.ApproximateNumberOfMessages.name)
        val response = connectionService.sqs.getQueueAttributes(queueUrl,attributesRequest)
        println(response)
    }

    fun receive(queueUrl: String, consume: Boolean, log: Boolean = true): MutableList<Message> {
        val queueResponse: MutableList<Message> = mutableListOf()
        try {
            if (log) {
                logService.info("Fetching Queue")
            }
            val receiveMessageRequest = ReceiveMessageRequest(queueUrl)
                .withWaitTimeSeconds(WAIT_TIME_SECONDS)
                .withMaxNumberOfMessages(MAX_NUMBER_OF_MESSAGES)

            val sqsMessages: MutableList<Message>? =
                connectionService.sqs.receiveMessage(receiveMessageRequest).messages

            sqsMessages?.forEach {
                queueResponse.add(it)
                if (consume) {
                    connectionService.sqs.deleteMessage(queueUrl, it.receiptHandle)
                }
            }
            if (log) {
                logService.warn("Listing last ${queueResponse.size} messages")
            }
            return queueResponse
        } catch (ex: AmazonSQSException) {
            logService.error(ex.message)
            throw ex
        }
    }

    fun createQueue(queueNme: String) {
        val createStandardQueueRequest = CreateQueueRequest(queueNme)
        connectionService.sqs.createQueue(createStandardQueueRequest)
    }

    fun reprocessDlq(queueUrl: String, dlqUrl: String, delay: Long) {
        logService.warn("Reprocessing DQL")
        logService.warn("Source: $dlqUrl")
        logService.warn("Target: $queueUrl")
        getQueueInfo(dlqUrl)
        var counter = 0
        do {
            val dlqMessages = receive(queueUrl = dlqUrl, consume = true, log = false)
            dlqMessages.forEach {
                send(queueUrl = queueUrl, message = it.body.trim(), delay = 1, log = false)
                Thread.sleep(delay)
                counter++
                logService.info("$counter processed messages")
            }
        } while (dlqMessages.size > 0)
        logService.warn("$counter messages reprocessed")
        logService.warn("Reprocessing DLQ Finished !")
    }
}
