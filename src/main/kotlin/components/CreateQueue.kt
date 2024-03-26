package components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import commons.DefaultColors.backgroundColor
import commons.DefaultColors.buttonColor
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import service.CommunicationService
import service.ConnectionService
import service.GenericSqsService
import service.VariableStore

@OptIn(DelicateCoroutinesApi::class)
@Suppress("LongMethod")
@Composable
fun createQueue(
    connectionService: ConnectionService,
    communicationService: CommunicationService,
    variableStore: VariableStore
) {

    val buttonModifier = Modifier.padding(10.dp)
    val defaultButtonColor = ButtonDefaults.buttonColors(backgroundColor = buttonColor, contentColor = Color.Black)

    var createQueueName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.background(backgroundColor)
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
                    enabled = (createQueueName.isNotEmpty()),
                    colors = defaultButtonColor,
                    onClick = {
                        GlobalScope.launch {
                            GenericSqsService(connectionService, communicationService)
                                .createQueue(createQueueName)
                            variableStore.createQueue = !variableStore.createQueue
                        }
                    }
                ) {
                    Text("Create Queue")
                }
            }
        }
    }
}
