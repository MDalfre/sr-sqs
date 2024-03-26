package service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.amazonaws.services.sqs.model.Message
import model.Queue
import model.SqsMock


class VariableStore {

    val version = "v3.1.0"

    var mockMode by mutableStateOf(false)

    //ConnectionForm
    private val connectionSettings = FileHandleService().readConfigFile()
    var serverUrl by mutableStateOf(connectionSettings.serverUrl)
    var accessKey by mutableStateOf(connectionSettings.accessKey)
    var secretKey by mutableStateOf(connectionSettings.secretKey)
    var sessionKey by mutableStateOf(connectionSettings.sessionKey)
    var selectedRegion by mutableStateOf(connectionSettings.serverRegion.name)
    var selectedCredential by mutableStateOf(connectionSettings.credentialType.name)
    var expandedRegion by mutableStateOf(false)
    var expandedCredential by mutableStateOf(false)

    //SendMessageForm
    var message by mutableStateOf("")
    var selectedUrlToSend by mutableStateOf("")
    var selectedQueueToSend by mutableStateOf(" ")
    var expandedToSend by mutableStateOf(false)

    //ReceiveMessageForm
    var selectedQueueToReceive by mutableStateOf(" ")
    var selectedUrlToReceive by mutableStateOf("")
    var expandedToReceive by mutableStateOf(false)
    var deleteMessage by mutableStateOf(true)
    var receivedMessages by mutableStateOf(listOf<Message>())

    //MockMode
    var mockList by mutableStateOf(listOf<SqsMock>())
    var sourceMessage by mutableStateOf("*")
    var targetMessage by mutableStateOf("")
    var expandedMockSource by mutableStateOf(false)
    var expandedMockTarget by mutableStateOf(false)
    var mockServiceRunning by mutableStateOf(false)
    var selectedMockQueueSource by mutableStateOf(" ")
    var selectedMockQueueTarget by mutableStateOf(" ")
    var selectedMockUrlSource by mutableStateOf("")
    var selectedMockUrlTarget by mutableStateOf("")

    //Buttons and TextFields
    var queues by mutableStateOf(listOf<Queue>())
    var connecting by mutableStateOf(false)

    //TopBar
    var expanded by mutableStateOf(false)
    var reprocess by mutableStateOf(false)
    var createQueue by mutableStateOf(false)
}
