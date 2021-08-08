package components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amazonaws.services.sqs.model.Message
import model.Queue
import service.ConnectionService
import service.GenericProducerService


var connectionService: ConnectionService? = null

@Composable
fun mainView() {

    val buttonModifier = Modifier.padding(10.dp)

    var expandedToSend by remember { mutableStateOf(false) }
    var expandedToReceive by remember { mutableStateOf(false) }

    val iconOne = if (expandedToSend)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    val iconTwo = if (expandedToReceive)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    var selectedQueueToSend by remember { mutableStateOf("") }
    var selectedUrlToSend by remember { mutableStateOf("") }
    var selectedQueueToReceive by remember { mutableStateOf("") }
    var selectedUrlToReceive by remember { mutableStateOf("") }

    var connecting by remember { mutableStateOf(false) }
    var queues by remember { mutableStateOf(listOf<Queue>()) }
    var receivedMessages by remember { mutableStateOf(listOf<Message>()) }
    val listState = rememberLazyListState()
    var serverUrl by remember { mutableStateOf("http://localhost:4566") }
    var accessKey by remember { mutableStateOf("docker") }
    var secretKey by remember { mutableStateOf("docker") }

    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
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

        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                enabled = !connecting,
                modifier = buttonModifier,
                onClick = {
                    Thread {
                        connecting = true
                        connectionService = ConnectionService(serverUrl, accessKey, secretKey)
                        queues = GenericProducerService(connectionService!!).getQueues()
                    }.start()
                }) {
                Text("Connect")
            }
        }


    }
    Column(
        modifier = Modifier.width(450.dp)
    ) {
        Row {
            OutlinedTextField(
                modifier = Modifier.padding(top = 10.dp).fillMaxWidth(),
                value = selectedQueueToSend,
                onValueChange = { selectedQueueToSend = it },
                label = { Text("Queues") },
                textStyle = TextStyle(fontSize = 13.sp),
                trailingIcon = {
                    Icon(iconOne, "contentDescription", Modifier.clickable { expandedToSend = !expandedToSend })
                }
            )
            DropdownMenu(
                modifier = Modifier.width(450.dp),
                expanded = expandedToSend,
                onDismissRequest = { expandedToSend = false },
            ) {
                queues.forEach { queueName ->
                    DropdownMenuItem(onClick = {
                        selectedQueueToSend = queueName.name
                        selectedUrlToSend = queueName.url
                        expandedToSend = !expandedToSend
                    }) {
                        Text(text = queueName.name, style = TextStyle(fontSize = 13.sp))
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
            Modifier,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                modifier = buttonModifier.padding(bottom = 16.dp),
                onClick = {
                    Thread {
                        GenericProducerService(connectionService!!).send(
                            selectedUrlToSend,
                            message,
                            1
                        )
                    }.start()
                }
            ) {
                Text("Send Message")
            }
        }
    }
    Column(
        modifier = Modifier.width(450.dp).padding(start = 16.dp, bottom = 16.dp)
    ) {
        Row {
            OutlinedTextField(
                modifier = Modifier.padding(top = 10.dp).fillMaxWidth(),
                value = selectedQueueToReceive,
                onValueChange = { selectedQueueToReceive = it },
                label = { Text("Queues") },
                textStyle = TextStyle(fontSize = 13.sp),
                trailingIcon = {
                    Icon(iconTwo, "contentDescription", Modifier.clickable { expandedToReceive = !expandedToReceive })
                }
            )
            DropdownMenu(
                modifier = Modifier.width(450.dp),
                expanded = expandedToReceive,
                onDismissRequest = { expandedToReceive = false },
            ) {
                queues.forEach { queueName ->
                    DropdownMenuItem(onClick = {
                        selectedQueueToReceive = queueName.name
                        selectedUrlToReceive = queueName.url
                        expandedToReceive = !expandedToReceive
                    }) {
                        Text(text = queueName.name, style = TextStyle(fontSize = 13.sp))
                    }
                }
            }
        }
        /** STARTS HERE **/
        Column(
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(
                text = "Received Messages",
                modifier = Modifier.padding(start = 3.dp),
                style = TextStyle(fontSize = 13.sp),
                color = Color.Gray
            )
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .height(580.dp)
                    .border(0.5.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
                    .height(30.dp),
                state = listState,
            ) {
                items(receivedMessages) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
                            .clickable {

                            },
                        elevation = 10.dp
                    ) {
                        Column {
                            Text("MessageId: ${it.messageId}")
                            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                                Text(it.body, style = MaterialTheme.typography.body2)
                            }
                        }
                    }
                }
            }


        }
        /** ENDS HERE **/
        Row(
            Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                modifier = buttonModifier,
                onClick = {
                    Thread {
                        receivedMessages = receivedMessages.plus(
                            GenericProducerService(connectionService!!).receive(selectedUrlToReceive)
                        )
                    }.start()
                }
            ) {
                Text("Receive Messages")
            }
        }
    }
}

