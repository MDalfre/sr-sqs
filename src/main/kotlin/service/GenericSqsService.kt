package service

import com.amazonaws.SdkClientException
import com.amazonaws.services.sqs.model.AmazonSQSException
import com.amazonaws.services.sqs.model.CreateQueueRequest
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.QueueAttributeName
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.amazonaws.services.sqs.model.SendMessageRequest
import commons.Constants.DEFAULT_WAIT_INTERVAL
import model.ProcessStatusEnum
import model.Queue


const val WAIT_TIME_SECONDS = 1
const val MAX_NUMBER_OF_MESSAGES = 10

class GenericSqsService(
    private val connectionService: ConnectionService,
    private val communicationService: CommunicationService
) {
    fun send(queueUrl: String, message: String, delay: Int, log: Boolean = true) {
        val messageList = message.split("//")
        if (log) {
            communicationService.logInfo("Found ${messageList.size} to send")
        }
        messageList.forEach {
            try {
                val sendMessageRequest = SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(it.trim())
                    .withDelaySeconds(delay)

                connectionService.sqs.sendMessage(sendMessageRequest)
            } catch (ex: AmazonSQSException) {
                communicationService.logError(ex.message)
                throw ex
            }
        }
        if (log) {
            communicationService.logSuccess("Messages sent !")
        }
    }

    fun getQueues(log: Boolean = true): List<Queue> {
        val queueResponse: MutableList<Queue> = mutableListOf()
        try {
            if (log) {
                communicationService.logInfo("Retrieving queues ...")
            }
            connectionService.sqs.listQueues().queueUrls.forEach {
                queueResponse.add(
                    Queue(
                        name = it.substringAfterLast("/"),
                        url = it
                    )
                )
            }
            if (log) communicationService.logSuccess("Queues loaded successfully")
            return queueResponse.sortedBy { it.name }
        } catch (ex: SdkClientException) {
            println(ex.message)
            communicationService.logError(ex.message)
            throw ex
        }
    }

    fun getQueueSize(queueUrl: String): Int {
        val attribute = QueueAttributeName.ApproximateNumberOfMessages.name
        val attributesRequest = listOf(attribute)
        val response = connectionService.sqs.getQueueAttributes(queueUrl, attributesRequest)
        return response.attributes.getValue(attribute).toInt()
    }

    fun receive(queueUrl: String, consume: Boolean, log: Boolean = true): MutableList<Message> {
        val queueResponse: MutableList<Message> = mutableListOf()
        if (log) {
            communicationService.logInfo("Fetching Queue ...")
        }
        do {
            val receiveMessageRequest = ReceiveMessageRequest(queueUrl)
                .withWaitTimeSeconds(WAIT_TIME_SECONDS)
                .withMaxNumberOfMessages(MAX_NUMBER_OF_MESSAGES)

            val sqsMessages: MutableList<Message> =
                connectionService.sqs.receiveMessage(receiveMessageRequest).messages

            sqsMessages.forEach {
                queueResponse.add(it)
                if (consume) {
                    connectionService.sqs.deleteMessage(queueUrl, it.receiptHandle)
                }
            }

            Thread.sleep(DEFAULT_WAIT_INTERVAL)

        } while (sqsMessages.size > 0)
        if (log) {
            communicationService.logSuccess("Listing last ${queueResponse.size} messages")
        }
        return queueResponse
    }

    fun createQueue(queueNme: String) {
        val createStandardQueueRequest = CreateQueueRequest(queueNme)
        connectionService.sqs.createQueue(createStandardQueueRequest)
    }

    fun reprocessDlq(queueUrl: String, dlqUrl: String, delay: Long) {
        communicationService.logWarn("Reprocessing DQL")
        communicationService.reprocessingDql = ProcessStatusEnum.STARTED
        getQueueSize(dlqUrl)
        var counter = 0
        do {
            val dlqMessages = receive(queueUrl = dlqUrl, consume = true, log = false)
            dlqMessages.forEach {
                send(queueUrl = queueUrl, message = it.body.trim(), delay = 1, log = false)
                Thread.sleep(delay)
                counter++
                communicationService.messageCounter = counter
            }
        } while (dlqMessages.size > 0)
        communicationService.messageCounter = 0
        communicationService.reprocessingDql = ProcessStatusEnum.COMPLETED
        communicationService.logSuccess("$counter messages reprocessed")
        communicationService.logSuccess("Reprocessing DLQ Finished !")
    }
}
