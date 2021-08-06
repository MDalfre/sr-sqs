package service

import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.amazonaws.services.sqs.model.SendMessageRequest


class GenericProducerService(
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

    fun getQueues(): MutableList<String>? {
        return connectionService.sqs.listQueues().queueUrls
    }

    fun receive(queueUrl: String){
        val receiveMessageRequest = ReceiveMessageRequest(queueUrl)
            .withWaitTimeSeconds(10)
            .withMaxNumberOfMessages(10)

        val sqsMessages: MutableList<Message>? = connectionService.sqs.receiveMessage(receiveMessageRequest).messages

        sqsMessages?.forEach {
            println(it)
        }
    }
}