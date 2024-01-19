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
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import commons.Constants
import commons.DefaultColors
import commons.DefaultColors.dropDownColor
import connectionService
import model.SqsMock
import model.ui.Theme
import service.CommunicationService
import service.FileHandleService
import service.GenericSqsService
import service.MockSqsService
import service.VariableStore

@Suppress("LongMethod", "ComplexMethod")
@Composable
fun mockMode(variableStore: VariableStore, communicationService: CommunicationService, theme: Theme) {
    val listStateMessages = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 16.dp, top = 35.dp)
    ) {
        Text(
            text = "Mock List",
            modifier = Modifier.padding(start = 3.dp),
            style = TextStyle(fontSize = 13.sp),
            color = DefaultColors.secondaryColor
        )
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .height(280.dp)
                .border(0.5.dp, color = DefaultColors.secondaryColor, shape = RoundedCornerShape(5.dp))
                .height(30.dp),
            state = listStateMessages

        ) {
            items(variableStore.mockList) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp),
                    backgroundColor = DefaultColors.secondaryColor,
                    elevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier.padding(5.dp)
                    ) {

                        Text("Awaiting on: ${it.sourceQueue.substringAfterLast("/")}")
                        Text("Respond on: ${it.targetQueue.substringAfterLast("/")}")
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                            Text(
                                "Awaiting for: ${it.messageToWait.take(Constants.DEFAULT_MAX_CHARACTER)}",
                                style = MaterialTheme.typography.body2
                            )
                            Text(
                                "Response: ${it.mockResponse.take(Constants.DEFAULT_MAX_CHARACTER)}",
                                style = MaterialTheme.typography.body2
                            )
                            Row(
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    "Menu",
                                    tint = Color.Black,
                                    modifier = Modifier.clickable {
                                        variableStore.mockList = variableStore.mockList.minus(it)
                                    }
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
                    modifier = Modifier.clickable(enabled = communicationService.sqsConnected) {
                        if (connectionService != null) {
                            variableStore.queues = GenericSqsService(connectionService!!, communicationService)
                                .getQueues(log = false)
                        }
                        variableStore.expandedMockSource = !variableStore.expandedMockSource
                    }.width(450.dp),
                    text = "Await on:",
                    value = variableStore.selectedMockQueueSource,
                    onValueChange = { variableStore.selectedMockQueueSource = it }
                )
                DropdownMenu(
                    modifier = Modifier.width(420.dp).heightIn(10.dp, 200.dp).background(dropDownColor),
                    expanded = variableStore.expandedMockSource,
                    onDismissRequest = { variableStore.expandedMockSource = false },
                ) {
                    variableStore.queues.forEach { queueName ->
                        DropdownMenuItem(
                            modifier = Modifier.padding(5.dp).height(15.dp),
                            onClick = {
                                variableStore.selectedMockQueueSource = queueName.name
                                variableStore.selectedMockUrlSource = queueName.url
                                variableStore.expandedMockSource = !variableStore.expandedMockSource
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
                    modifier = Modifier.clickable(enabled = communicationService.sqsConnected) {
                        if (connectionService != null) {
                            variableStore.queues = GenericSqsService(connectionService!!, communicationService)
                                .getQueues(log = false)
                        }
                        variableStore.expandedMockTarget = !variableStore.expandedMockTarget
                    }.width(450.dp),
                    text = "Mock response:",
                    value = variableStore.selectedMockQueueTarget,
                    onValueChange = { variableStore.selectedMockQueueTarget = it }
                )
                DropdownMenu(
                    modifier = Modifier.width(420.dp).heightIn(10.dp, 200.dp).background(dropDownColor),
                    expanded = variableStore.expandedMockTarget,
                    onDismissRequest = { variableStore.expandedMockTarget = false },
                ) {
                    variableStore.queues.forEach { queueName ->
                        DropdownMenuItem(
                            modifier = Modifier.padding(5.dp).height(15.dp),
                            onClick = {
                                variableStore.selectedMockQueueTarget = queueName.name
                                variableStore.selectedMockUrlTarget = queueName.url
                                variableStore.expandedMockTarget = !variableStore.expandedMockTarget
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
                        value = variableStore.sourceMessage,
                        onValueChange = { variableStore.sourceMessage = it },
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
                        value = variableStore.targetMessage,
                        onValueChange = { variableStore.targetMessage = it },
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
                modifier = theme.buttonModifier,
                enabled = variableStore.connecting && (variableStore.selectedMockQueueSource != " "),
                colors = theme.defaultButtonColor,
                onClick = {
                    variableStore.mockList = variableStore.mockList.plus(
                        SqsMock(
                            sourceQueue = variableStore.selectedMockUrlSource,
                            targetQueue = variableStore.selectedMockUrlTarget,
                            messageToWait = variableStore.sourceMessage.trim(),
                            mockResponse = variableStore.targetMessage.trim()
                        )
                    )
                    variableStore.sourceMessage = "*"
                    variableStore.targetMessage = ""
                    variableStore.selectedMockQueueTarget = " "
                    variableStore.selectedMockQueueSource = " "
                }
            ) {
                Text("Create Mock")
            }
            Button(
                modifier = theme.buttonModifier,
                enabled = (variableStore.mockList.isNotEmpty() && !variableStore.mockServiceRunning),
                colors = theme.defaultButtonColor,
                onClick = {
                    variableStore.mockServiceRunning = !variableStore.mockServiceRunning
                    MockSqsService(connectionService!!, communicationService).startMockService(variableStore.mockList)
                }
            ) {
                Text("Start Mock")
            }
            Button(
                modifier = theme.buttonModifier,
                enabled = variableStore.mockServiceRunning,
                colors = theme.defaultButtonColor,
                onClick = {
                    variableStore.mockServiceRunning = !variableStore.mockServiceRunning
                    MockSqsService(connectionService!!, communicationService).stopMockService()
                }
            ) {
                Text("Stop Mock")
            }
            Button(
                modifier = theme.buttonModifier,
                enabled = variableStore.connecting,
                colors = theme.defaultButtonColor,
                onClick = {
                    val currentQueues = GenericSqsService(
                        connectionService = requireNotNull(connectionService) { "SQS service not connected" },
                        communicationService = communicationService
                    ).getQueues(log = false)
                    val mockFile = FileHandleService().importSqsMock(currentQueues)
                    variableStore.mockList = variableStore.mockList.plus(mockFile)
                }
            ) {
                Text("Import")
            }
        }
    }
}
