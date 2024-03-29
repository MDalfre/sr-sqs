package components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import commons.DefaultColors
import commons.DefaultColors.dropDownColor
import commons.Util
import connectionService
import service.CommunicationService
import service.FileHandleService
import service.GenericSqsService
import service.VariableStore

@Suppress("LongMethod")
@Composable
fun topBar(communicationService: CommunicationService, variableStore: VariableStore) {

//    var expanded by remember { mutableStateOf(false) }
//    var reprocess by remember { mutableStateOf(false) }
//    var createQueue by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = Modifier.height(30.dp),
        backgroundColor = Color.Black
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Menu",
                tint = Color.White,
                modifier = Modifier.clickable { variableStore.expanded = !variableStore.expanded }
            )
            DropdownMenu(
                modifier = Modifier.background(dropDownColor),
                expanded = variableStore.expanded,
                onDismissRequest = { variableStore.expanded = !variableStore.expanded }
            ) {
                DropdownMenuItem(
                    modifier = Modifier.padding(5.dp).height(15.dp),
                    enabled = (connectionService != null),
                    onClick = {
                        variableStore.expanded = !variableStore.expanded
                        variableStore.createQueue = !variableStore.createQueue
                    }
                ) {
                    Text("Create Queue", style = TextStyle(fontSize = 10.sp))
                }
                Divider()
                DropdownMenuItem(
                    modifier = Modifier.padding(5.dp).height(15.dp),
                    enabled = (connectionService != null),
                    onClick = {
                        variableStore.expanded = !variableStore.expanded
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
                        variableStore.expanded = !variableStore.expanded
                        variableStore.reprocess = !variableStore.reprocess
                    }
                ) {
                    Text("Reprocess DLQ", style = TextStyle(fontSize = 10.sp))
                }
            }

            if (variableStore.createQueue) {
                DialogWindow(
                    onCloseRequest = { variableStore.createQueue = !variableStore.createQueue },
                    title = "Create Queue",
                    state = rememberDialogState(size = DpSize(ALERT_WIDTH.dp, ALERT_SMALL_HEIGHT.dp)),
                    icon = Util.appIcon(),
                    content = {
                        createQueue(
                            connectionService = connectionService!!,
                            communicationService = communicationService,
                            variableStore = variableStore
                        )
                    }
                )
            }
            if (variableStore.reprocess) {
                DialogWindow(
                    onCloseRequest = { variableStore.reprocess = !variableStore.reprocess },
                    title = "Reprocess DLQ",
                    state = rememberDialogState(size = DpSize(ALERT_WIDTH.dp, ALERT_HEIGHT.dp)),
                    icon = Util.appIcon(),
                    content = {
                        dlqReprocess(
                            connectionService = connectionService!!,
                            communicationService = communicationService
                        )
                    }
                )
            }
            Text(
                text = variableStore.version,
                modifier = Modifier.padding(top = 7.dp),
                style = TextStyle(fontSize = 13.sp),
                color = DefaultColors.tintColor
            )
        }
    }
}
