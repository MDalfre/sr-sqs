package service

import com.amazonaws.SdkClientException
import com.amazonaws.services.sqs.model.AmazonSQSException
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.amazonaws.services.sqs.model.SendMessageRequest
import model.Queue


class GenericProducerService(
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
            val receiveMessageRequest = ReceiveMessageRequest(queueUrl)
                .withWaitTimeSeconds(1)
                .withMaxNumberOfMessages(10)

            val sqsMessages: MutableList<Message>? =
                connectionService.sqs.receiveMessage(receiveMessageRequest).messages

            sqsMessages?.forEach {
                queueResponse.add(it)
            }
            return queueResponse
        } catch (ex: AmazonSQSException) {
            logService.error(ex.message)
            throw ex
        }
    }
}