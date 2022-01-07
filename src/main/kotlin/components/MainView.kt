package components

import androidx.compose.desktop.ComposeWindow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.v1.Dialog
import androidx.compose.ui.window.v1.DialogProperties
import com.amazonaws.regions.Regions
import com.amazonaws.services.sqs.model.Message
import commons.DefaultColors.backgroundBlue
import commons.DefaultColors.lightBlue
import commons.DefaultColors.orange
import commons.objectToJson
import connectionService
import model.ConnectionSettings
import model.CredentialType
import model.Log
import model.LogTypeEnum
import model.Queue
import service.CommunicationService
import service.ConnectionService
import service.FileHandleService
import service.GenericSqsService
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
    val defaultButtonColor = ButtonDefaults.buttonColors(backgroundColor = orange, contentColor = Color.Black)

    /* DropDownMenu */
    var expandedToSend by remember { mutableStateOf(false) }
    var expandedToReceive by remember { mutableStateOf(false) }
    var expandedRegion by remember { mutableStateOf(false) }
    var expandedCredential by remember { mutableStateOf(false) }
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
    var queues by remember { mutableStateOf(listOf<Queue>()) }
    var receivedMessages by remember { mutableStateOf(listOf<Message>()) }
    val listStateLog = rememberLazyListState()
    val listStateMessages = rememberLazyListState()
    var serverUrl by remember { mutableStateOf(connectionSettings.serverUrl) }
    var accessKey by remember { mutableStateOf(connectionSettings.accessKey) }
    var secretKey by remember { mutableStateOf(connectionSettings.secretKey) }
    var sessionKey by remember { mutableStateOf(connectionSettings.sessionKey) }
    var message by remember { mutableStateOf("") }
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
                text = "Credential Type",
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
        /** System log **/
        Column(
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(
                text = "System log",
                modifier = Modifier.padding(start = 3.dp),
                style = TextStyle(fontSize = 13.sp),
                color = lightBlue
            )
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .border(0.5.dp, color = lightBlue, shape = RoundedCornerShape(5.dp))
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
                        Divider(color = lightBlue)
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth()
                    .align(Alignment.Start)
            ) {
                Image(
                    imageResource("logo.png"),
                    contentDescription = "SR SQS",
                    alignment = Alignment.BottomStart
                )
            }
        }
        /** System log end **/
    }
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
                        GenericSqsService(connectionService!!, communicationService).send(selectedUrlToSend, message, 1)
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
                color = lightBlue
            )
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .height(580.dp)
                    .border(0.5.dp, color = lightBlue, shape = RoundedCornerShape(5.dp))
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
                        backgroundColor = lightBlue,
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
                onDismissRequest = { showAlert = !showAlert },
                properties = DialogProperties(title = "MessageId: $titleAlert", IntSize(ALERT_WIDTH, ALERT_HEIGHT)),
                content = {
                    Column(
                        Modifier.background(backgroundBlue).fillMaxSize()
                    ) {
                        Column(
                            Modifier.padding(16.dp).background(backgroundBlue)
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
                    color = lightBlue
                )
            }
        }
    }

    if (systemLog.size != communicationService.systemLog.size) {
        systemLog = communicationService.systemLog.map { it }.reversed()
    }
}
