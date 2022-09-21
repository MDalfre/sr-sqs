package service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import commons.Constants.DEFAULT_MOCK_INTERVAL
import commons.Constants.DEFAULT_WAIT_WILDCARD
import model.ProcessStatusEnum
import model.SqsMock

class MockSqsService(
    private val connectionService: ConnectionService,
    private val communicationService: CommunicationService
) {

    private val genericSqsService = GenericSqsService(connectionService, communicationService)
    private val mapper = jacksonObjectMapper()

    fun startMockService(mockList: List<SqsMock>) {
        communicationService.mockService = ProcessStatusEnum.STARTED
        mockList.forEach {
            Thread {
                communicationService.logInfo("Starting mock for ${it.targetQueue.substringAfterLast("/")}")
                mockQueue(it)
            }.start()
        }
    }

    fun stopMockService() {
        communicationService.logInfo("Stopping mock for all queues")
        communicationService.mockService = ProcessStatusEnum.NOT_STARTED
    }

    private tailrec fun mockQueue(sqsMock: SqsMock) {
        genericSqsService.receive(queueUrl = sqsMock.sourceQueue, consume = true, log = false).let { messageList ->
            messageList.map { it.body }.forEach { body ->
                when (sqsMock.messageToWait) {
                    body.trim() -> sendMock(sqsMock, body)
                    DEFAULT_WAIT_WILDCARD -> sendMock(sqsMock, body)
                }
            }
        }
        Thread.sleep(DEFAULT_MOCK_INTERVAL)
        if (communicationService.mockService == ProcessStatusEnum.STARTED) {
            mockQueue(sqsMock)
        }
    }

    private fun sendMock(sqsMock: SqsMock, receivedMessage: String) {
        communicationService.logInfo("Sending mock to ${sqsMock.targetQueue}")
        val newSqsMock = replaceValuesInNodes(sqsMock,receivedMessage)
        genericSqsService.send(
            queueUrl = newSqsMock.targetQueue,
            message = newSqsMock.mockResponse,
            delay = 1,
            log = false
        )
    }

    private fun getResponseFields(message: String): MutableList<String> {
        val pattern = "%(.*?)%".toPattern()
        val matcher = pattern.matcher(message)
        val foundList = mutableListOf<String>()
        while (matcher.find()) {
            foundList.add(matcher.group().trim())
        }
        return foundList
    }

    private fun findValueInNode(wantedList: MutableList<String>, receivedMessage: String): MutableMap<String, Any> {
        val mappedReplaces = mutableMapOf<String, Any>()
        wantedList.forEach {
            mappedReplaces[it] = mapper.readTree(receivedMessage)
                .findValue(it.removePrefix("%").removeSuffix("%"))
        }
        return mappedReplaces
    }

    private fun replaceValuesInNodes(sqsMock: SqsMock, receivedMessage: String): SqsMock {
        val wantedList = getResponseFields(sqsMock.mockResponse)
        val mappedValues = findValueInNode(wantedList, receivedMessage)
        var mockResponse = sqsMock.mockResponse
        mappedValues.forEach {
            mockResponse = mockResponse.replace(it.key, it.value.toString())
        }
        return sqsMock.copy(mockResponse = mockResponse)
    }
}
