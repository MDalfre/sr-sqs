package service

import com.amazonaws.SdkClientException
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.amazonaws.services.sqs.model.SendMessageRequest
import model.Queue


class GenericProducerService(
    private val connectionService: ConnectionService,
    private val logService: LogService
) {
    fun send(queueUrl: String, message: String, delay: Int) {
        println(connectionService.sqs.listQueues().queueUrls)
        val sendMessageRequest = SendMessageRequest()
            .withQueueUrl(queueUrl)
            .withMessageBody(message)
            .withDelaySeconds(delay)

        connectionService.sqs.sendMessage(sendMessageRequest)
    }

    fun getQueues(): MutableList<Queue> {
        val queueResponse: MutableList<Queue> = mutableListOf()
        try {
            connectionService.sqs.listQueues().queueUrls.forEach {
                queueResponse.add(
                    Queue(
                        name = it.substringAfterLast("/"),
                        url = it
                    )
                )
            }
        } catch (ex: SdkClientException) {
            println(ex.message)
            logService.log(ex.message ?: "Internal Error")
            throw ex
        }
        return queueResponse
    }

    fun receive(queueUrl: String): MutableList<Message> {
        val queueResponse: MutableList<Message> = mutableListOf()
        val receiveMessageRequest = ReceiveMessageRequest(queueUrl)
            .withWaitTimeSeconds(1)
            .withMaxNumberOfMessages(10)

        val sqsMessages: MutableList<Message>? = connectionService.sqs.receiveMessage(receiveMessageRequest).messages

        sqsMessages?.forEach {
            queueResponse.add(it)
        }
        return queueResponse
    }

}