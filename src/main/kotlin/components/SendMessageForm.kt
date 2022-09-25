package components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import connectionService
import model.ui.Theme
import service.CommunicationService
import service.FileHandleService
import service.GenericSqsService
import service.VariableStore

@Suppress("LongMethod", "ComplexMethod")
@Composable
fun sendMessageFrom(
    variableStore: VariableStore,
    communicationService: CommunicationService,
    theme: Theme
) {
    Column(
        modifier = Modifier.width(450.dp).padding(top = 25.dp)
    ) {
        Row {
            defaultTextField(
                modifier = Modifier.clickable(enabled = communicationService.sqsConnected) {
                    if (connectionService != null) {
                        variableStore.queues =
                            GenericSqsService(connectionService!!, communicationService).getQueues(log = false)
                    }
                    variableStore.expandedToSend = !variableStore.expandedToSend
                },
                text = "Queues",
                value = variableStore.selectedQueueToSend,
                onValueChange = { variableStore.selectedQueueToSend = it }
            )
            DropdownMenu(
                modifier = Modifier.width(450.dp).heightIn(10.dp, 200.dp),
                expanded = variableStore.expandedToSend,
                onDismissRequest = { variableStore.expandedToSend = false },
            ) {
                variableStore.queues.forEach { queueName ->
                    DropdownMenuItem(
                        modifier = Modifier.padding(5.dp).height(15.dp),
                        onClick = {
                            variableStore.selectedQueueToSend = queueName.name
                            variableStore.selectedUrlToSend = queueName.url
                            variableStore.expandedToSend = !variableStore.expandedToSend
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
            value = variableStore.message,
            onValueChange = { variableStore.message = it },
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                modifier = theme.buttonModifier,
                enabled = variableStore.connecting && (variableStore.selectedQueueToSend != " "),
                colors = theme.defaultButtonColor,
                onClick = {
                    Thread {
                        GenericSqsService(connectionService!!, communicationService).send(
                            variableStore.selectedUrlToSend,
                            variableStore.message.replace("\n", "").replace(" ",""),
                            1
                        )
                    }.start()
                }
            ) {
                Text("Produce Message")
            }
            Button(
                modifier = theme.buttonModifier,
                enabled = variableStore.connecting && (variableStore.selectedQueueToSend != " "),
                colors = theme.defaultButtonColor,
                onClick = {
                    variableStore.message = FileHandleService().importMessage()
                }
            ) {
                Text("Import")
            }
        }
    }
}
