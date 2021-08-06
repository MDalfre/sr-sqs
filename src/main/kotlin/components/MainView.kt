package components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import service.ConnectionService
import service.GenericProducerService


var connectionService: ConnectionService? = null

@Composable
fun mainView() {

    val textFieldModifier = Modifier.fillMaxWidth().height(55.dp)
    val buttonModifier = Modifier.padding(10.dp)

    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }



    var text by remember { mutableStateOf("Hello, World!") }
    var queues = remember { mutableListOf("123","456") }
    var serverUrl by remember { mutableStateOf("http://localhost:4566") }
    var accessKey by remember { mutableStateOf("docker") }
    var secretKey by remember { mutableStateOf("docker") }

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 36.dp)
            .width(300.dp)
    ) {

        OutlinedTextField(
            modifier = textFieldModifier,
            value = serverUrl,
            singleLine = true,
            textStyle = TextStyle(fontSize = 13.sp),
            onValueChange = { value -> serverUrl = value },
            label = { Text("Server URL") },
        )
        OutlinedTextField(
            modifier = textFieldModifier,
            value = accessKey,
            singleLine = true,
            textStyle = TextStyle(fontSize = 13.sp),
            onValueChange = { value -> accessKey = value },
            label = { Text("Access Key") }
        )
        OutlinedTextField(
            modifier = textFieldModifier,
            value = secretKey,
            singleLine = true,
            textStyle = TextStyle(fontSize = 13.sp),
            onValueChange = { value -> secretKey = value },
            label = { Text("Secret Key") }
        )
        Button(
            modifier = buttonModifier,
            onClick = {
                connectionService = ConnectionService(serverUrl, accessKey, secretKey)
            }) {
            Text("Connect")
        }
        Button(
            modifier = buttonModifier,
            onClick = {
                queues = GenericProducerService(connectionService!!).getQueues() ?: mutableListOf("Empty")
                println(queues)
            }) {
            Text("List Queues")
        }
        Button(
            modifier = buttonModifier,
            onClick = {
                GenericProducerService(connectionService!!).send(
                    "http://localhost:4566/queue/some-test-queue",
                    "ihaaaaaa",
                    1
                )
            }) {
            Text("Send Message")
        }
        Button(
            modifier = buttonModifier,
            onClick = {
                GenericProducerService(connectionService!!).receive("http://localhost:4566/queue/some-test-queue")
            }) {
            Text("Receive Messages")
        }
    }
    Column (
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 36.dp)
            .width(300.dp)
    ) {
        Row {
            queues.forEach {
                Text(it)
            }
        }
    }
}
