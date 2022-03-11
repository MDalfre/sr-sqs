package service

import commons.Constants.DEFAULT_MOCK_INTERVAL
import commons.Constants.DEFAULT_WAIT_WILDCARD
import model.ProcessStatusEnum
import model.SqsMock

class MockSqsService(
    private val connectionService: ConnectionService,
    private val communicationService: CommunicationService
) {

    private val genericSqsService = GenericSqsService(connectionService, communicationService)

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
        communicationService.mockService = ProcessStatusEnum.NOT_STARTED
    }

    private tailrec fun mockQueue(sqsMock: SqsMock) {
        genericSqsService.receive(queueUrl = sqsMock.sourceQueue, consume = true, log = false).let { messageList ->
            messageList.map { it.body }.forEach { body ->
                when (sqsMock.messageToWait) {
                    body.trim() -> sendMock(sqsMock)
                    DEFAULT_WAIT_WILDCARD -> sendMock(sqsMock)
                }
            }
        }
        Thread.sleep(DEFAULT_MOCK_INTERVAL)
        if (communicationService.mockService == ProcessStatusEnum.STARTED) {
            mockQueue(sqsMock)
        }
    }

    private fun sendMock(sqsMock: SqsMock) {
        communicationService.logInfo("Sending mock to ${sqsMock.targetQueue}")
        genericSqsService.send(queueUrl = sqsMock.targetQueue, message = sqsMock.mockResponse, delay = 1, log = false)
    }
}
