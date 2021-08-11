package service

import com.amazonaws.SdkClientException
import com.amazonaws.services.sqs.model.*
import model.Queue


const val WAIT_TIME_SECONDS = 1
const val MAX_NUMBER_OF_MESSAGES = 10

class GenericSqsService(
    private val connectionService: ConnectionService,
    private val logService: LogService
) {
    fun send(queueUrl: String, message: String, delay: Int) {
        try {
            logService.info("Sending message")
            val sendMessageRequest = SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(message)
                .withDelaySeconds(delay)

            connectionService.sqs.sendMessage(sendMessageRequest)
        } catch (ex: AmazonSQSException) {
            logService.error(ex.message)
            throw ex
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

    fun receive(queueUrl: String): MutableList<Message> {
        val queueResponse: MutableList<Message> = mutableListOf()
        try {
            logService.info("Fetching Queue")
            val receiveMessageRequest = ReceiveMessageRequest(queueUrl)
                .withWaitTimeSeconds(WAIT_TIME_SECONDS)
                .withMaxNumberOfMessages(MAX_NUMBER_OF_MESSAGES)

            val sqsMessages: MutableList<Message>? =
                connectionService.sqs.receiveMessage(receiveMessageRequest).messages

            sqsMessages?.forEach {
                queueResponse.add(it)
                connectionService.sqs.deleteMessage(queueUrl, it.receiptHandle)
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
}
