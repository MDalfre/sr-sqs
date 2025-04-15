package components

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
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.ContentAlpha
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import commons.Constants.DEFAULT_MAX_CHARACTER
import commons.DefaultColors
import commons.DefaultColors.dropDownColor
import commons.DefaultSizes.DEFAULT_QUEUE_END
import commons.DefaultSizes.DEFAULT_QUEUE_START
import commons.DefaultSizes.DEFAULT_PADDING_TOP
import commons.DefaultSizes.LAZY_COLUM_SIZE
import commons.Util
import commons.prettyJson
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
fun ReceiveMessageForm(theme: Theme) {

    val listStateMessages = rememberLazyListState()
    var showAlert by remember { mutableStateOf(false) }
    var titleAlert by remember { mutableStateOf(" ") }
    var bodyAlert by remember { mutableStateOf(" ") }

    Column(
        modifier = Modifier.fillMaxWidth().padding(
            start = DEFAULT_QUEUE_START,
            end = DEFAULT_QUEUE_END,
            top = DEFAULT_PADDING_TOP
        )
    ) {
        DefaultTextField(
            modifier = Modifier.clickable(enabled = communicationService.sqsConnected) {
                if (connectionService != null) {
                    variableStore.queues =
                        GenericSqsService(connectionService!!, communicationService).getQueues(log = false)
                }
                variableStore.expandedToReceive = !variableStore.expandedToReceive
            },
            text = "Queues",
            value = variableStore.selectedQueueToReceive,
            onValueChange = { variableStore.selectedQueueToReceive = it },
            loading = communicationService.gettingQueues == ProcessingStatus.STARTED && variableStore.connecting
        )
        Row {
            DropdownMenu(
                modifier = Modifier.width(450.dp).heightIn(10.dp, 200.dp).background(dropDownColor),
                expanded = variableStore.expandedToReceive,
                onDismissRequest = { variableStore.expandedToReceive = false },
            ) {
                variableStore.queues.forEach { queueName ->
                    DropdownMenuItem(
                        modifier = Modifier.padding(5.dp).height(15.dp),
                        onClick = {
                            variableStore.selectedQueueToReceive = queueName.name
                            variableStore.selectedUrlToReceive = queueName.url
                            variableStore.expandedToReceive = !variableStore.expandedToReceive
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
                color = DefaultColors.secondaryColor
            )
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .height(LAZY_COLUM_SIZE)
                    .border(0.5.dp, color = DefaultColors.secondaryColor, shape = RoundedCornerShape(5.dp))
                    .height(30.dp),
                state = listStateMessages

            ) {
                items(variableStore.receivedMessages) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
                            .clickable {
                                titleAlert = it.messageId
                                bodyAlert = it.body.prettyJson()
                                showAlert = true
                            },
                        backgroundColor = DefaultColors.secondaryColor,
                        elevation = 10.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(5.dp)
                        ) {
                            Text("id: ${it.messageId}")
                            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                                Text(it.body.take(DEFAULT_MAX_CHARACTER), style = MaterialTheme.typography.body2)
                            }
                        }
                    }
                }
            }
        }
        if (showAlert) {
            DialogWindow(
                onCloseRequest = { showAlert = !showAlert },
                title = "MessageId: $titleAlert",
                state = rememberDialogState(size = DpSize(ALERT_WIDTH.dp, ALERT_HEIGHT.dp)),
                icon = Util.appIcon(),
                content = {
                    Column(
                        Modifier.background(DefaultColors.backgroundColor).fillMaxSize()
                    ) {
                        Column(
                            Modifier.padding(16.dp).background(DefaultColors.backgroundColor)
                        ) {
                            defaultTextEditor(
                                modifier = Modifier.height(300.dp),
                                text = "Message body",
                                value = bodyAlert,
                                onValueChange = {},
                            )
                            Row(
                                Modifier.background(DefaultColors.backgroundColor).fillMaxSize().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    modifier = theme.buttonModifier,
                                    enabled = true,
                                    colors = theme.defaultButtonColor,
                                    onClick = {
                                        variableStore.message = bodyAlert.trimIndent()
                                    }
                                ) {
                                    Text("Copy to send message")
                                }
                            }
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
                modifier = theme.buttonModifier,
                enabled = variableStore.connecting && (variableStore.selectedQueueToReceive != " "),
                colors = theme.defaultButtonColor,
                onClick = {
                    GlobalScope.launch {
                        val result = GenericSqsService(connectionService!!, communicationService)
                            .receive(
                                queueUrl = variableStore.selectedUrlToReceive,
                                consume = variableStore.deleteMessage
                            )
                        variableStore.receivedMessages = result
                    }
                }
            ) {
                Text("Consume Messages")
            }
            Button(
                modifier = theme.buttonModifier,
                enabled = variableStore.receivedMessages.isNotEmpty(),
                colors = theme.defaultButtonColor,
                onClick = {
                    GlobalScope.launch {
                        FileHandleService().dumpMessageToFile(variableStore.receivedMessages, communicationService)
                    }
                }
            ) {
                Text("Dump")
            }
            Row(modifier = Modifier.padding(all = 1.dp).height(60.dp)) {
                Checkbox(
                    modifier = theme.buttonModifier.padding(all = 2.dp),
                    checked = variableStore.deleteMessage,
                    onCheckedChange = { variableStore.deleteMessage = !variableStore.deleteMessage },
                )
                Text(
                    text = "Delete messages",
                    modifier = Modifier.padding(top = 15.dp),
                    style = TextStyle(fontSize = 13.sp),
                    color = DefaultColors.secondaryColor
                )
            }
        }
    }
}
