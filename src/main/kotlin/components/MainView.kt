package components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import model.Queue
import service.ConnectionService
import service.GenericProducerService


var connectionService: ConnectionService? = null

@Composable
fun mainView() {

    val buttonModifier = Modifier.padding(10.dp)

    var expandedToSend by remember { mutableStateOf(false) }
    var expandedToReceive by remember { mutableStateOf(false) }
    val icon = if (expandedToSend)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    var selectedQueueToSend by remember { mutableStateOf("") }
    var selectedUrlToSend by remember { mutableStateOf("") }
    var selectedQueueToReceive by remember { mutableStateOf("") }
    var selectedUrlToReceive by remember { mutableStateOf("") }

    var connecting by remember { mutableStateOf(false) }
    var queues by remember { mutableStateOf(listOf<Queue>()) }
    var receivedMessages by mutableStateOf(listOf<String>())
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
        modifier = Modifier.width(500.dp)
    ) {
        Row {
            OutlinedTextField(
                modifier = Modifier.padding(top = 10.dp).fillMaxWidth(),
                value = selectedQueueToSend,
                onValueChange = { selectedQueueToSend = it },
                label = { Text("Queues") },
                textStyle = TextStyle(fontSize = 13.sp),
                trailingIcon = {
                    Icon(icon, "contentDescription", Modifier.clickable { expandedToSend = !expandedToSend })
                }
            )
            DropdownMenu(
                modifier = Modifier.width(500.dp),
                expanded = expandedToSend,
                onDismissRequest = { expandedToSend = false },
            ) {
                queues.forEach { queueName ->
                    DropdownMenuItem(onClick = {
                        selectedQueueToSend = queueName.name
                        selectedUrlToSend = queueName.url
                        expandedToSend = !expandedToSend
                    }) {
                        Text(text = queueName.name)
                    }
                }
            }
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().height(600.dp)
                .padding(bottom = 16.dp),
            value = message,
            onValueChange = { message = it },
            label = { Text("Send Message") }
        )
        Row(
            Modifier,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                modifier = buttonModifier.padding(bottom = 16.dp),
                onClick = {
                    GenericProducerService(connectionService!!).send(
                        selectedUrlToSend,
                        message,
                        1
                    )
                }) {
                Text("Send Message")
            }
        }
    }
    Column(
        modifier = Modifier.width(500.dp)
    ) {
        Row {
            OutlinedTextField(
                modifier = Modifier.padding(top = 10.dp).fillMaxWidth(),
                value = selectedQueueToReceive,
                onValueChange = { selectedQueueToReceive = it },
                label = { Text("Queues") },
                textStyle = TextStyle(fontSize = 13.sp),
                trailingIcon = {
                    Icon(icon, "contentDescription", Modifier.clickable { expandedToReceive = !expandedToReceive })
                }
            )
            DropdownMenu(
                modifier = Modifier.width(500.dp),
                expanded = expandedToReceive,
                onDismissRequest = { expandedToReceive = false },
            ) {
                queues.forEach { queueName ->
                    DropdownMenuItem(onClick = {
                        selectedQueueToReceive = queueName.name
                        selectedUrlToReceive = queueName.url
                        expandedToReceive = !expandedToReceive
                    }) {
                        Text(text = queueName.name)
                    }
                }
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(600.dp)
                .padding(bottom = 16.dp),
        ) {
            items(receivedMessages) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 26.dp, end = 5.dp, top = 5.dp, bottom = 5.dp),
                    elevation = 10.dp
                ) {
                    Text(it)
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
                onClick = {
                    receivedMessages = receivedMessages.plus(GenericProducerService(connectionService!!).receive(selectedUrlToReceive).map { it })
                }) {
                Text("Receive Messages")
            }
        }
    }
}

