package components

import androidx.compose.foundation.background
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
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import commons.DefaultColors
import commons.DefaultColors.dropDownColor
import commons.DefaultSizes.DEFAULT_PADDING_TOP
import commons.DefaultSizes.LAZY_COLUM_SIZE
import communicationService
import connectionService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.ProcessingStatus
import model.ui.Theme
import service.FileHandleService
import service.GenericSqsService
import variableStore

@OptIn(DelicateCoroutinesApi::class)
@Suppress("LongMethod", "ComplexMethod")
@Composable
fun SendMessageFrom(theme: Theme) {
    Column(
        modifier = Modifier.width(450.dp).padding(top = DEFAULT_PADDING_TOP)
    ) {
        DefaultTextField(
            modifier = Modifier.clickable(enabled = communicationService.sqsConnected) {
                if (connectionService != null) {
                    variableStore.queues =
                        GenericSqsService(connectionService!!, communicationService).getQueues(log = false)
                }
                variableStore.expandedToSend = !variableStore.expandedToSend
            },
            text = "Queues",
            value = variableStore.selectedQueueToSend,
            onValueChange = { variableStore.selectedQueueToSend = it },
            loading = communicationService.gettingQueues == ProcessingStatus.STARTED && variableStore.connecting
        )
        Row {
            DropdownMenu(
                modifier = Modifier.width(450.dp).heightIn(10.dp, 200.dp).background(dropDownColor),
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
            modifier = Modifier.height(LAZY_COLUM_SIZE),
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
                    GlobalScope.launch {
                        GenericSqsService(connectionService!!, communicationService).send(
                            variableStore.selectedUrlToSend,
                            variableStore.message.replace("\n", "").replace(" ", ""),
                            1
                        )
                    }
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
            Button(
                modifier = theme.buttonModifier,
                enabled = variableStore.connecting && (variableStore.selectedQueueToSend != " "),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = DefaultColors.alertButtonColor,
                    contentColor = Color.Black
                ),
                onClick = {
                    GlobalScope.launch {
                        GenericSqsService(connectionService!!, communicationService).purgeQueue(
                            variableStore.selectedUrlToSend,
                            variableStore.selectedQueueToSend
                        )
                    }
                }
            ) {
                Text("Purge")
            }
        }
    }
}
