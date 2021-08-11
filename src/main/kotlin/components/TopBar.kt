package components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
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
                    onClick = {
                        expanded = !expanded
                        createQueue = !createQueue
                    }
                ) {
                    Text("Menu 1", style = TextStyle(fontSize = 10.sp))
                }
                DropdownMenuItem(
                    modifier = Modifier.padding(5.dp).height(15.dp),
                    onClick = {

                    }
                ) {
                    Text("Menu 2", style = TextStyle(fontSize = 10.sp))
                }
                DropdownMenuItem(
                    modifier = Modifier.padding(5.dp).height(15.dp),
                    onClick = {

                    }
                ) {
                    Text("Menu 3", style = TextStyle(fontSize = 10.sp))
                }

            }

            if (createQueue) {
                Dialog(
                    onDismissRequest = {},
                    properties = DialogProperties(title = "Create Queue", IntSize(ALERT_WIDTH, ALERT_HEIGHT)),
                    content = {
                        Column(
                            modifier = Modifier.background(backgroundBlue)
                                .fillMaxSize(),
                        ) {
                            Column {
                                defaultTextEditor(
                                    text = "Queue name",
                                    value = createQueueName,
                                    onValueChange = { createQueueName = it }
                                )
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
                )
            }
        }
    }

}
