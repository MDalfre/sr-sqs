package service

import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.amazonaws.services.sqs.model.SendMessageRequest
import kotlinx.coroutines.CoroutineScope
import model.Queue
import kotlin.coroutines.CoroutineContext


class GenericProducerService (
    private val connectionService: ConnectionService
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
        connectionService.sqs.listQueues().queueUrls.forEach {
            queueResponse.add(
                Queue(
                    name = it.substringAfterLast("/"),
                    url = it
                )
            )
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