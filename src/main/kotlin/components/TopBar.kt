package components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.v1.Dialog
import androidx.compose.ui.window.v1.DialogProperties
import commons.DefaultColors
import connectionService
import service.CommunicationService
import service.FileHandleService
import service.GenericSqsService

@Suppress("LongMethod")
@Composable
fun topBar(communicationService: CommunicationService) {

    var expanded by remember { mutableStateOf(false) }
    var reprocess by remember { mutableStateOf(false) }
    var createQueue by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = Modifier.height(30.dp),
        backgroundColor = Color.Black
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
                            val foundQueues = GenericSqsService(connectionService!!, communicationService).getQueues()
                            FileHandleService().createFile(foundQueues)
                        }
                    }
                ) {
                    Text("Create localstack init.sh", style = TextStyle(fontSize = 10.sp))
                }
                Divider()
                DropdownMenuItem(
                    modifier = Modifier.padding(5.dp).height(15.dp),
                    enabled = (connectionService != null),
                    onClick = {
                        expanded = !expanded
                        reprocess = !reprocess
                    }
                ) {
                    Text("Reprocess DLQ", style = TextStyle(fontSize = 10.sp))
                }
            }

            if (createQueue) {
                Dialog(
                    onDismissRequest = { createQueue = !createQueue },
                    properties = DialogProperties(title = "Create Queue", IntSize(ALERT_WIDTH, ALERT_SMALL_HEIGHT)),
                    content = {
                        createQueue(
                            connectionService = connectionService!!,
                            communicationService = communicationService
                        )
                    }
                )
            }
            if (reprocess) {
                Dialog(
                    onDismissRequest = { reprocess = !reprocess },
                    properties = DialogProperties(title = "Reprocess DLQ", IntSize(ALERT_WIDTH, ALERT_HEIGHT)),
                    content = {
                        dlqReprocess(
                            connectionService = connectionService!!,
                            communicationService = communicationService
                        )
                    }
                )
            }
            Text(
                text = "v2.0.0",
                modifier = Modifier.padding(top = 7.dp),
                style = TextStyle(fontSize = 13.sp),
                color = DefaultColors.tintColor
            )
        }
    }
}
