package components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import com.amazonaws.regions.Regions
import com.amazonaws.services.sqs.model.Message
import commons.DefaultColors.backgroundColor
import commons.DefaultColors.buttonColor
import commons.DefaultColors.secondaryColor
import commons.Util
import commons.objectToJson
import connectionService
import model.ConnectionSettings
import model.CredentialType
import model.Log
import model.LogTypeEnum
import model.Queue
import model.SqsMock
import service.CommunicationService
import service.ConnectionService
import service.FileHandleService
import service.GenericSqsService
import service.MockSqsService
import java.io.FilenameFilter

const val ALERT_WIDTH = 600
const val ALERT_HEIGHT = 450
const val ALERT_SMALL_HEIGHT = 250

@Suppress("LongMethod", "ComplexMethod")
@Composable
fun mainView(
    communicationService: CommunicationService,
    connectionSettings: ConnectionSettings
) {

    /** States **/
    /* Styles */
    val buttonModifier = Modifier.padding(10.dp)
    val defaultButtonColor = ButtonDefaults.buttonColors(backgroundColor = buttonColor, contentColor = Color.Black)

    /* DropDownMenu */
    var expandedToSend by remember { mutableStateOf(false) }
    var expandedToReceive by remember { mutableStateOf(false) }
    var expandedRegion by remember { mutableStateOf(false) }
    var expandedCredential by remember { mutableStateOf(false) }
    var expandedMockSource by remember { mutableStateOf(false) }
    var expandedMockTarget by remember { mutableStateOf(false) }
    var mockServiceRunning by remember { mutableStateOf(false) }
    var selectedMockQueueSource by remember { mutableStateOf(" ") }
    var selectedMockQueueTarget by remember { mutableStateOf(" ") }
    var selectedMockUrlSource by remember { mutableStateOf("") }
    var selectedMockUrlTarget by remember { mutableStateOf("") }
    var selectedQueueToSend by remember { mutableStateOf(" ") }
    var selectedQueueToReceive by remember { mutableStateOf(" ") }
    var selectedUrlToSend by remember { mutableStateOf("") }
    var selectedUrlToReceive by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf(connectionSettings.serverRegion.name) }
    var selectedCredential by remember { mutableStateOf(connectionSettings.credentialType.name) }

    /* AlertDialog */
    var showAlert by remember { mutableStateOf(false) }
    var titleAlert by remember { mutableStateOf(" ") }
    var bodyAlert by remember { mutableStateOf(" ") }

    /* Button&TextFields */
    var connecting by remember { mutableStateOf(false) }
    var deleteMessage by remember { mutableStateOf(true) }
    var mockMode by remember { mutableStateOf(false) }
    var queues by remember { mutableStateOf(listOf<Queue>()) }
    var receivedMessages by remember { mutableStateOf(listOf<Message>()) }
    var mockList by remember { mutableStateOf(listOf<SqsMock>()) }
    val listStateLog = rememberLazyListState()
    val listStateMessages = rememberLazyListState()
    var serverUrl by remember { mutableStateOf(connectionSettings.serverUrl) }
    var accessKey by remember { mutableStateOf(connectionSettings.accessKey) }
    var secretKey by remember { mutableStateOf(connectionSettings.secretKey) }
    var sessionKey by remember { mutableStateOf(connectionSettings.sessionKey) }
    var message by remember { mutableStateOf("") }
    var sourceMessage by remember { mutableStateOf("*") }
    var targetMessage by remember { mutableStateOf("") }
    var systemLog by mutableStateOf(listOf<Log>())

    /** End of States **/

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 25.dp)
            .width(300.dp)
    ) {
        Row {
            defaultTextField(
                modifier = Modifier.clickable { expandedCredential = !expandedCredential },
                text = "Credential Type1",
                value = selectedCredential,
                onValueChange = { selectedCredential = it }
            )
            DropdownMenu(
                expanded = expandedCredential,
                onDismissRequest = { expandedCredential = !expandedCredential }
            ) {
                CredentialType.values().forEach {
                    DropdownMenuItem(
                        modifier = Modifier.padding(5.dp).height(15.dp),
                        onClick = {
                            selectedCredential = it.name
                            expandedCredential = !expandedCredential
                        }
                    ) {
                        Text(text = it.name, style = TextStyle(fontSize = 10.sp))
                    }
                }
            }
        }
        defaultTextField(
            text = "Server URL",
            value = serverUrl,
            onValueChange = { serverUrl = it }
        )
        defaultTextField(
            text = "Access Key",
            value = accessKey,
            onValueChange = { accessKey = it }
        )
        defaultTextField(
            text = "Secret Key",
            value = secretKey,
            onValueChange = { secretKey = it }
        )
        if (selectedCredential == CredentialType.SESSION.name) {
            defaultTextField(
                text = "Session Key",
                value = sessionKey,
                onValueChange = { sessionKey = it }
            )
        }
        Row {
            defaultTextField(
                modifier = Modifier.clickable { expandedRegion = !expandedRegion },
                text = "Region",
                value = selectedRegion,
                onValueChange = { selectedRegion = it }
            )
            DropdownMenu(
                expanded = expandedRegion,
                onDismissRequest = { expandedRegion = !expandedRegion }
            ) {
                Regions.values().forEach {
                    DropdownMenuItem(
                        modifier = Modifier.padding(5.dp).height(15.dp),
                        onClick = {
                            selectedRegion = it.name
                            expandedRegion = !expandedRegion
                        }
                    ) {
                        Text(text = it.name, style = TextStyle(fontSize = 10.sp))
                    }
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                enabled = !connecting,
                colors = defaultButtonColor,
                modifier = buttonModifier,
                onClick = {
                    val settings = ConnectionSettings(
                        credentialType = CredentialType.valueOf(selectedCredential),
                        serverUrl = serverUrl,
                        accessKey = accessKey,
                        secretKey = secretKey,
                        sessionKey = sessionKey,
                        serverRegion = Regions.valueOf(selectedRegion)
                    )
                    FileHandleService().createConfigFile(settings)

                    Thread {
                        connecting = true
                        connectionService = ConnectionService(
                            connectionSettings = settings,
                            communicationService = communicationService
                        )
                        queues = GenericSqsService(
                            connectionService = requireNotNull(connectionService) { "SQS service not connected" },
                            communicationService = communicationService
                        ).getQueues()
                    }.start()
                }) {
                Text("Connect")
            }
            Button(
                enabled = connecting,
                colors = defaultButtonColor,
                modifier = buttonModifier,
                onClick = {
                    Thread {
                        connecting = false
                        connectionService!!.disconnect()
                    }.start()
                }) {
                Text("Disconnect")
            }
        }
        Row(
            Modifier
                .fillMaxWidth().border(0.5.dp, color = secondaryColor, shape = RoundedCornerShape(5.dp)),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Mock Mode:",
                modifier = Modifier.padding(top = 7.dp),
                style = TextStyle(fontSize = 13.sp),
                color = secondaryColor
            )
            Switch(
                modifier = Modifier.padding(top = 3.dp, bottom = 3.dp),
                checked = mockMode,
                onCheckedChange = { mockMode = !mockMode }
            )
        }
        /** System log **/
        Column(
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(
                text = "System log",
                modifier = Modifier.padding(start = 3.dp),
                style = TextStyle(fontSize = 13.sp),
                color = secondaryColor
            )
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .border(0.5.dp, color = secondaryColor, shape = RoundedCornerShape(5.dp))
                    .height(30.dp),
                state = listStateLog,
            ) {
                items(systemLog) { logMessage ->
                    val color = when (logMessage.type) {
                        LogTypeEnum.INFO -> Color.Gray
                        LogTypeEnum.WARN -> Color.Yellow
                        LogTypeEnum.ERROR -> Color.Red
                        LogTypeEnum.SUCCESS -> Color.Green
                    }
                    Column(
                        Modifier.padding(5.dp)
                    ) {
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                            Text(logMessage.message, style = MaterialTheme.typography.caption, color = color)
                        }
                        Divider(color = secondaryColor)
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth()
                    .align(Alignment.Start)
            ) {
                Image(
                    Util.appLogo(),
                    contentDescription = "SR SQS",
                    alignment = Alignment.BottomStart
                )
            }
        }
        /** System log end **/
    }

    if (mockMode == false) {
        /* Center Column */
        Column(
            modifier = Modifier.width(450.dp).padding(top = 25.dp)
        ) {
            Row {
                defaultTextField(
                    modifier = Modifier.clickable {
                        if (connectionService != null) {
                            queues = GenericSqsService(connectionService!!, communicationService).getQueues()
                        }
                        expandedToSend = !expandedToSend
                    },
                    text = "Queues",
                    value = selectedQueueToSend,
                    onValueChange = { selectedQueueToSend = it }
                )
                DropdownMenu(
                    modifier = Modifier.width(450.dp).heightIn(10.dp, 200.dp),
                    expanded = expandedToSend,
                    onDismissRequest = { expandedToSend = false },
                ) {
                    queues.forEach { queueName ->
                        DropdownMenuItem(
                            modifier = Modifier.padding(5.dp).height(15.dp),
                            onClick = {
                                selectedQueueToSend = queueName.name
                                selectedUrlToSend = queueName.url
                                expandedToSend = !expandedToSend
                            }
                        ) {
                            Text(text = queueName.name, style = TextStyle(fontSize = 12.sp))
                        }
                    }
                }
            }
            defaultTextEditor(
                modifier = Modifier.height(580.dp),
                text = "Send Message",
                value = message,
                onValueChange = { message = it },
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    modifier = buttonModifier,
                    enabled = connecting && (selectedQueueToSend != " "),
                    colors = defaultButtonColor,
                    onClick = {
                        Thread {
                            GenericSqsService(connectionService!!, communicationService).send(
                                selectedUrlToSend,
                                message,
                                1
                            )
                        }.start()
                    }
                ) {
                    Text("Produce Message")
                }
                Button(
                    modifier = buttonModifier,
                    enabled = connecting && (selectedQueueToSend != " "),
                    colors = defaultButtonColor,
                    onClick = {
                        communicationService.logInfo("Importing file ...")
                        val filePicker = java.awt.FileDialog(ComposeWindow())
                        filePicker.filenameFilter = FilenameFilter { _, name -> name.endsWith(".srsqs") }
                        filePicker.isVisible = true
                        val file = "${filePicker.directory}${filePicker.file}"
                        if (filePicker.file != null) {
                            message = FileHandleService().importFile(file)
                            communicationService.logSuccess("Imported: ${filePicker.file}")
                        }
                    }
                ) {
                    Text("Import")
                }
            }
        }
        /* Right Column */
        Column(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 25.dp)
        ) {
            Row {
                defaultTextField(
                    modifier = Modifier.clickable {
                        if (connectionService != null) {
                            queues = GenericSqsService(connectionService!!, communicationService).getQueues()
                        }
                        expandedToReceive = !expandedToReceive
                    },
                    text = "Queues",
                    value = selectedQueueToReceive,
                    onValueChange = { selectedQueueToReceive = it }
                )
                DropdownMenu(
                    modifier = Modifier.width(450.dp).heightIn(10.dp, 200.dp),
                    expanded = expandedToReceive,
                    onDismissRequest = { expandedToReceive = false },
                ) {
                    queues.forEach { queueName ->
                        DropdownMenuItem(
                            modifier = Modifier.padding(5.dp).height(15.dp),
                            onClick = {
                                selectedQueueToReceive = queueName.name
                                selectedUrlToReceive = queueName.url
                                expandedToReceive = !expandedToReceive
                            }
                        ) {
                            Text(text = queueName.name, style = TextStyle(fontSize = 12.sp))
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text(
                    text = "Received Messages",
                    modifier = Modifier.padding(start = 3.dp),
                    style = TextStyle(fontSize = 13.sp),
                    color = secondaryColor
                )
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .height(580.dp)
                        .border(0.5.dp, color = secondaryColor, shape = RoundedCornerShape(5.dp))
                        .height(30.dp),
                    state = listStateMessages

                ) {
                    items(receivedMessages) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
                                .clickable {
                                    titleAlert = it.messageId
                                    bodyAlert = it.objectToJson() ?: "Fail to serialize"
                                    showAlert = true
                                },
                            backgroundColor = secondaryColor,
                            elevation = 10.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Text("id: ${it.messageId}")
                                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                                    Text(it.body, style = MaterialTheme.typography.body2)
                                }
                            }

                        }
                    }
                }
            }
            if (showAlert) {
                Dialog(
                    onCloseRequest = { showAlert = !showAlert },
                    title = "MessageId: $titleAlert",
                    state = rememberDialogState(size = DpSize(ALERT_WIDTH.dp, ALERT_HEIGHT.dp)),
                    icon = Util.appIcon(),
                    content = {
                        Column(
                            Modifier.background(backgroundColor).fillMaxSize()
                        ) {
                            Column(
                                Modifier.padding(16.dp).background(backgroundColor)
                            ) {
                                defaultTextEditor(
                                    modifier = Modifier.height(300.dp),
                                    text = "SQS message details",
                                    value = bodyAlert,
                                    onValueChange = {},
                                )
                            }
                        }
                    }
                )
            }
            Row(
                Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    modifier = buttonModifier,
                    enabled = connecting && (selectedQueueToReceive != " "),
                    colors = defaultButtonColor,
                    onClick = {
                        Thread {
                            val result = GenericSqsService(connectionService!!, communicationService)
                                .receive(queueUrl = selectedUrlToReceive, consume = deleteMessage)
                            receivedMessages = result
                        }.start()
                    }
                ) {
                    Text("Consume Messages")
                }
                Button(
                    modifier = buttonModifier,
                    enabled = receivedMessages.isNotEmpty(),
                    colors = defaultButtonColor,
                    onClick = {
                        Thread {
                            FileHandleService().dumpMessageToFile(receivedMessages, communicationService)
                        }.start()
                    }
                ) {
                    Text("Dump")
                }
                Row(modifier = Modifier.padding(all = 1.dp).height(60.dp)) {
                    Checkbox(
                        modifier = buttonModifier.padding(all = 2.dp),
                        checked = deleteMessage,
                        onCheckedChange = { deleteMessage = !deleteMessage },
                    )
                    Text(
                        text = "Delete messages",
                        modifier = Modifier.padding(top = 15.dp),
                        style = TextStyle(fontSize = 13.sp),
                        color = secondaryColor
                    )
                }
            }
        }
    } else {
        /* Mock Mode */
        Column(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 16.dp, top = 35.dp)
        ) {
            Text(
                text = "Mock List",
                modifier = Modifier.padding(start = 3.dp),
                style = TextStyle(fontSize = 13.sp),
                color = secondaryColor
            )
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .border(0.5.dp, color = secondaryColor, shape = RoundedCornerShape(5.dp))
                    .height(30.dp),
                state = listStateMessages

            ) {
                items(mockList) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp),
                        backgroundColor = secondaryColor,
                        elevation = 10.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(5.dp)
                        ) {

                            Text("Awaiting on: ${it.sourceQueue.substringAfterLast("/")}")
                            Text("Respond on: ${it.targetQueue.substringAfterLast("/")}")
                            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                                Text("Awaiting for: ${it.messageToWait}", style = MaterialTheme.typography.body2)
                                Text("Response: ${it.mockResponse}", style = MaterialTheme.typography.body2)
                                Row(
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        "Menu",
                                        tint = Color.Black,
                                        modifier = Modifier.clickable { mockList = mockList.minus(it) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier.width(450.dp).padding(start = 16.dp, end = 16.dp, top = 10.dp)
                )
                {
                    defaultTextField(
                        modifier = Modifier.clickable {
                            if (connectionService != null) {
                                queues = GenericSqsService(connectionService!!, communicationService).getQueues()
                            }
                            expandedMockSource = !expandedMockSource
                        }.width(450.dp),
                        text = "Await on:",
                        value = selectedMockQueueSource,
                        onValueChange = { selectedMockQueueSource = it }
                    )
                    DropdownMenu(
                        modifier = Modifier.width(420.dp).heightIn(10.dp, 200.dp),
                        expanded = expandedMockSource,
                        onDismissRequest = { expandedMockSource = false },
                    ) {
                        queues.forEach { queueName ->
                            DropdownMenuItem(
                                modifier = Modifier.padding(5.dp).height(15.dp),
                                onClick = {
                                    selectedMockQueueSource = queueName.name
                                    selectedMockUrlSource = queueName.url
                                    expandedMockSource = !expandedMockSource
                                }
                            ) {
                                Text(text = queueName.name, style = TextStyle(fontSize = 12.sp))
                            }
                        }
                    }

                }
                Column(
                    modifier = Modifier.width(450.dp).padding(start = 16.dp, end = 16.dp, top = 10.dp)
                ) {
                    defaultTextField(
                        modifier = Modifier.clickable {
                            if (connectionService != null) {
                                queues = GenericSqsService(connectionService!!, communicationService).getQueues()
                            }
                            expandedMockTarget = !expandedMockTarget
                        }.width(450.dp),
                        text = "Mock response:",
                        value = selectedMockQueueTarget,
                        onValueChange = { selectedMockQueueTarget = it }
                    )
                    DropdownMenu(
                        modifier = Modifier.width(420.dp).heightIn(10.dp, 200.dp),
                        expanded = expandedMockTarget,
                        onDismissRequest = { expandedMockTarget = false },
                    ) {
                        queues.forEach { queueName ->
                            DropdownMenuItem(
                                modifier = Modifier.padding(5.dp).height(15.dp),
                                onClick = {
                                    selectedMockQueueTarget = queueName.name
                                    selectedMockUrlTarget = queueName.url
                                    expandedMockTarget = !expandedMockTarget
                                }
                            ) {
                                Text(text = queueName.name, style = TextStyle(fontSize = 12.sp))
                            }
                        }
                    }

                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier.width(450.dp).padding(start = 16.dp, end = 16.dp, top = 25.dp)
                ) {
                    Row {
                        defaultTextEditor(
                            modifier = Modifier.height(200.dp),
                            text = "Wait for:",
                            value = sourceMessage,
                            onValueChange = { sourceMessage = it },
                        )
                    }

                }
                Column(
                    modifier = Modifier.width(450.dp).padding(start = 16.dp, end = 16.dp, top = 25.dp)
                ) {
                    Row {
                        defaultTextEditor(
                            modifier = Modifier.height(200.dp),
                            text = "Response message:",
                            value = targetMessage,
                            onValueChange = { targetMessage = it },
                        )
                    }
                }
            }
            Row(
                Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    modifier = buttonModifier,
                    enabled = connecting && (selectedMockQueueSource != " "),
                    colors = defaultButtonColor,
                    onClick = {
                        mockList = mockList.plus(
                            SqsMock(
                                sourceQueue = selectedMockUrlSource,
                                targetQueue = selectedMockUrlTarget,
                                messageToWait = sourceMessage.trim(),
                                mockResponse = targetMessage.trim()
                            )
                        )
                    }
                ) {
                    Text("Create Mock")
                }
                Button(
                    modifier = buttonModifier,
                    enabled = (mockList.isNotEmpty() && !mockServiceRunning),
                    colors = defaultButtonColor,
                    onClick = {
                        mockServiceRunning = !mockServiceRunning
                        MockSqsService(connectionService!!, communicationService).startMockService(mockList)
                    }
                ) {
                    Text("Start Mock")
                }
                Button(
                    modifier = buttonModifier,
                    enabled = mockServiceRunning,
                    colors = defaultButtonColor,
                    onClick = {
                        mockServiceRunning = !mockServiceRunning
                        MockSqsService(connectionService!!, communicationService).stopMockService()
                    }
                ) {
                    Text("Stop Mock")
                }
            }
        }

    }

    if (systemLog.size != communicationService.systemLog.size) {
        systemLog = communicationService.systemLog.map { it }.reversed()
    }
}
