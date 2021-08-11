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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.v1.Dialog
import androidx.compose.ui.window.v1.DialogProperties
import commons.backgroundBlue
import commons.orange
import connectionService
import service.FileHandleService
import service.GenericSqsService
import service.LogService


@Composable
fun topBar(logService: LogService) {

    val buttonModifier = Modifier.padding(10.dp)
    val defaultButtonColor = ButtonDefaults.buttonColors(backgroundColor = orange, contentColor = Color.Black)

    var expanded by remember { mutableStateOf(false) }
    var createQueue by remember { mutableStateOf(false) }
    var createQueueName by remember { mutableStateOf("") }

    TopAppBar(
        modifier = Modifier.height(30.dp),
        backgroundColor = Color.Black
    ) {
        Row {
            Icon(
                imageVector = Icons.Default.Menu,
                "Menu",
                tint = Color.White,
                modifier = Modifier.clickable { expanded = !expanded }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = !expanded }
            ) {
                DropdownMenuItem(
                    modifier = Modifier.padding(5.dp).height(15.dp),
                    enabled = (connectionService != null),
                    onClick = {
                        expanded = !expanded
                        createQueue = !createQueue
                    }
                ) {
                    Text("Create Queue", style = TextStyle(fontSize = 10.sp))
                }
                Divider()
                DropdownMenuItem(
                    modifier = Modifier.padding(5.dp).height(15.dp),
                    enabled = (connectionService != null),
                    onClick = {
                        expanded = !expanded
                        if (connectionService != null) {
                            val queues = GenericSqsService(connectionService!!, logService).getQueues()
                            FileHandleService().createFile(queues)
                        }
                    }
                ) {
                    Text("Create localstack init.sh", style = TextStyle(fontSize = 10.sp))
                }
            }

            if (createQueue) {
                Dialog(
                    onDismissRequest = { createQueue = !createQueue },
                    properties = DialogProperties(title = "Create Queue", IntSize(ALERT_WIDTH, ALERT_SMALL_HEIGHT)),
                    content = {
                        Column(
                            modifier = Modifier.background(backgroundBlue)
                                .fillMaxSize(),
                        ) {
                            Column(
                                modifier = Modifier.padding(50.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                defaultTextEditor(
                                    text = "Queue name",
                                    value = createQueueName,
                                    onValueChange = { createQueueName = it }
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(
                                        modifier = buttonModifier,
                                        enabled = (createQueueName.isNotEmpty() && connectionService != null),
                                        colors = defaultButtonColor,
                                        onClick = {
                                            Thread {
                                                GenericSqsService(connectionService!!, logService)
                                                    .createQueue(createQueueName)
                                            }.start()
                                        }
                                    ) {
                                        Text("Create Queue")
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }

}
