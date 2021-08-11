package components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import commons.backgroundBlue
import commons.lightBlue
import commons.objectToJson
import commons.orange
import connectionService
import model.Log
import model.LogType
import model.Queue
import service.ConnectionService
import service.GenericSqsService
import service.LogService

const val ALERT_WIDTH = 600
const val ALERT_HEIGHT = 450


@Suppress("LongMethod")
@Composable
fun mainView(
    logService: LogService
) {

    /** States **/
    /* Styles */
    val buttonModifier = Modifier.padding(10.dp)
    val defaultButtonColor = ButtonDefaults.buttonColors(backgroundColor = orange, contentColor = Color.Black)

    /* DropDownMenu */
    var expandedToSend by remember { mutableStateOf(false) }
    var expandedToReceive by remember { mutableStateOf(false) }
    var expandedRegion by remember { mutableStateOf(false) }
    var selectedQueueToSend by remember { mutableStateOf(" ") }
    var selectedQueueToReceive by remember { mutableStateOf(" ") }
    var selectedUrlToSend by remember { mutableStateOf("") }
    var selectedUrlToReceive by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf(Regions.US_EAST_1.name) }

    /* AlertDialog */
    var showAlert by remember { mutableStateOf(false) }
    var titleAlert by remember { mutableStateOf(" ") }
    var bodyAlert by remember { mutableStateOf(" ") }

    /* Button&TextFields */
    var connecting by remember { mutableStateOf(false) }
    var queues by remember { mutableStateOf(listOf<Queue>()) }
    var receivedMessages by remember { mutableStateOf(listOf<Message>()) }
    val listState = rememberLazyListState()
    var serverUrl by remember { mutableStateOf("http://localhost:4566") }
    var accessKey by remember { mutableStateOf("docker") }
    var secretKey by remember { mutableStateOf("docker") }
    var message by remember { mutableStateOf("") }
    var systemLog by remember { mutableStateOf(listOf<Log>()) }

    /** End of States **/

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 25.dp)
            .width(300.dp)
    ) {

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
                    Thread {
                        connecting = true
                        connectionService = ConnectionService(serverUrl, accessKey, secretKey, logService)
                        queues = GenericSqsService(connectionService!!, logService).getQueues()
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
                state = listState,
            ) {
                items(systemLog) { logMessage ->
                    val color = when (logMessage.type) {
                        LogType.INFO -> Color.Gray
                        LogType.WARN -> Color.Yellow
                        LogType.ERROR -> Color.Red
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
                modifier = Modifier.clickable { expandedToSend = !expandedToSend },
                text = "Queues",
                value = selectedQueueToSend,
                onValueChange = { selectedQueueToSend = it }
            )
            DropdownMenu(
                modifier = Modifier.width(450.dp),
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
                        GenericSqsService(connectionService!!, logService).send(selectedUrlToSend, message, 1)
                    }.start()
                }
            ) {
                Text("Produce Message")
            }
        }
    }
    /* Right Column */
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 25.dp)
    ) {
        Row {
            defaultTextField(
                modifier = Modifier.clickable { expandedToReceive = !expandedToReceive },
                text = "Queues",
                value = selectedQueueToReceive,
                onValueChange = { selectedQueueToReceive = it }
            )
            DropdownMenu(
                modifier = Modifier.width(450.dp),
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
                state = listState,
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
                            Text("Id: ${it.messageId}")
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
                        val result = GenericSqsService(connectionService!!, logService)
                            .receive(selectedUrlToReceive)
                        receivedMessages = result
                    }.start()
                }
            ) {
                Text("Consume Messages")
            }
        }
    }

    if (systemLog.size != logService.systemLog.size) {
        systemLog = logService.systemLog.map { it }
    }
}

