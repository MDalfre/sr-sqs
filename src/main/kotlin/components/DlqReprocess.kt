package components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import commons.Constants.DEFAULT_LOADING_RANGE_END
import commons.Constants.DEFAULT_LOADING_RANGE_START
import commons.Constants.DEFAULT_LOADING_START
import commons.Constants.DEFAULT_TIME_INTERVAL
import commons.Constants.ZERO
import commons.DefaultColors.backgroundColor
import commons.DefaultColors.buttonColor
import commons.DefaultColors.dropDownColor
import commons.DefaultColors.secondaryColor
import commons.Util.toPercentage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.ProcessStatusEnum
import model.Queue
import service.CommunicationService
import service.ConnectionService
import service.GenericSqsService

@OptIn(DelicateCoroutinesApi::class)
@Suppress("LongMethod", "ComplexMethod")
@Composable
fun dlqReprocess(connectionService: ConnectionService, communicationService: CommunicationService) {

    val buttonModifier = Modifier.padding(10.dp)
    val defaultButtonColor = ButtonDefaults.buttonColors(backgroundColor = buttonColor, contentColor = Color.Black)

    var queues by remember { mutableStateOf(listOf<Queue>()) }
    var selectedSourceQueue by remember { mutableStateOf(" ") }
    var selectedTargetQueue by remember { mutableStateOf(" ") }
    var selectedSourceUrl by remember { mutableStateOf("") }
    var selectedTargetUrl by remember { mutableStateOf("") }
    var expandedSourceDropbox by remember { mutableStateOf(false) }
    var expandedTargetDropbox by remember { mutableStateOf(false) }
    var sourceQueueSize by remember { mutableStateOf(ZERO) }
    var timeInterval by remember { mutableStateOf(DEFAULT_TIME_INTERVAL) }
    var loading by remember { mutableStateOf(DEFAULT_LOADING_START) }
    var reprocessStart by remember { mutableStateOf(false) }
    var messageCounter by mutableStateOf(ZERO)
    var completedMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.background(backgroundColor)
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.padding(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) { //SOURCE QUEUE
            Row {
                if (!reprocessStart) {
                    defaultTextField(
                        modifier =
                        Modifier.clickable {
                            queues = GenericSqsService(connectionService, communicationService).getQueues()
                            expandedSourceDropbox = !expandedSourceDropbox
                        },
                        text = "Source Queue",
                        value = selectedSourceQueue,
                        enabled = !reprocessStart,
                        onValueChange = { selectedSourceQueue = it }
                    )
                    DropdownMenu(
                        modifier = Modifier.width(450.dp).height(100.dp).background(dropDownColor),
                        expanded = expandedSourceDropbox,
                        onDismissRequest = { expandedSourceDropbox = false },
                    ) {
                        queues.forEach { queueName ->
                            DropdownMenuItem(
                                modifier = Modifier.padding(5.dp).height(15.dp),
                                onClick = {
                                    selectedSourceQueue = queueName.name
                                    selectedSourceUrl = queueName.url
                                    expandedSourceDropbox = !expandedSourceDropbox
                                    sourceQueueSize = GenericSqsService(connectionService, communicationService)
                                        .getQueueSize(selectedSourceUrl)
                                }
                            ) {
                                Text(text = queueName.name, style = TextStyle(fontSize = 12.sp))
                            }
                        }
                    }
                }
            }
            if (selectedSourceUrl.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Source queue approximated item count: $sourceQueueSize",
                        modifier = Modifier.padding(top = 5.dp),
                        style = TextStyle(fontSize = 13.sp),
                        color = secondaryColor
                    )
                }
            }
            Row {//TARGET QUEUE

                if (!reprocessStart) {
                    defaultTextField(
                        modifier = Modifier.clickable {
                            queues = GenericSqsService(connectionService, communicationService).getQueues()
                            expandedTargetDropbox = !expandedTargetDropbox
                        },
                        text = "Target Queue",
                        value = selectedTargetQueue,
                        enabled = !reprocessStart,
                        onValueChange = {
                            if (!reprocessStart) {
                                selectedTargetQueue = it
                            }
                        }
                    )
                }
                DropdownMenu(
                    modifier = Modifier.width(450.dp).height(100.dp).background(dropDownColor),
                    expanded = expandedTargetDropbox,
                    onDismissRequest = { expandedTargetDropbox = false },
                ) {
                    queues.forEach { queueName ->
                        DropdownMenuItem(
                            modifier = Modifier.padding(5.dp).height(15.dp),
                            onClick = {
                                selectedTargetQueue = queueName.name
                                selectedTargetUrl = queueName.url
                                expandedTargetDropbox = !expandedTargetDropbox
                            }
                        ) {
                            Text(text = queueName.name, style = TextStyle(fontSize = 12.sp))
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (!reprocessStart) {
                    Text(
                        text = "Dispatch delay: ${timeInterval.toLong()} ms",
                        modifier = Modifier.padding(top = 5.dp),
                        style = TextStyle(fontSize = 13.sp),
                        color = secondaryColor
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().height(15.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (!reprocessStart) {
                    Slider(
                        valueRange = DEFAULT_LOADING_RANGE_START..DEFAULT_LOADING_RANGE_END,
                        onValueChange = { timeInterval = it },
                        value = timeInterval,
                        colors = SliderDefaults.colors(
                            thumbColor = buttonColor,
                            activeTrackColor = secondaryColor
                        )
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (reprocessStart) {
                    Text(
                        text = "${loading.toPercentage()}%",
                        modifier = Modifier,
                        style = TextStyle(fontSize = 13.sp),
                        color = secondaryColor
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (reprocessStart) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(50.dp).padding(20.dp),
                        progress = loading,
                        color = buttonColor
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = completedMessage,
                    modifier = Modifier,
                    style = TextStyle(fontSize = 13.sp),
                    color = Color.Green
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (!reprocessStart) {
                    Button(
                        modifier = buttonModifier,
                        enabled = (selectedTargetUrl.isNotEmpty() && selectedSourceUrl.isNotEmpty() && !reprocessStart),
                        colors = defaultButtonColor,
                        onClick = {
                            completedMessage = ""
                            GlobalScope.launch {
                                GenericSqsService(connectionService, communicationService)
                                    .reprocessDlq(
                                        queueUrl = selectedTargetUrl,
                                        dlqUrl = selectedSourceUrl,
                                        delay = timeInterval.toLong()
                                    )
                            }
                            reprocessStart = true
                            messageCounter = 1
                        }
                    ) {
                        Text("Reprocess")
                    }
                }
            }
        }
    }

    if (communicationService.reprocessingDql == ProcessStatusEnum.COMPLETED) {
        communicationService.messageCounter = 1
        completedMessage = "Reprocessing completed"
        reprocessStart = !reprocessStart
        communicationService.reprocessingDql = ProcessStatusEnum.NOT_STARTED
    }

    if (messageCounter != communicationService.messageCounter) {
        messageCounter = communicationService.messageCounter
    }

    if (reprocessStart) {
        val iterator = 1f / sourceQueueSize
        loading = messageCounter * iterator
    }
}
