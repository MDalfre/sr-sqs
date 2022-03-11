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
import service.CommunicationService
import service.ConnectionService
import service.GenericSqsService

@Suppress("LongMethod")
@Composable
fun createQueue(connectionService: ConnectionService, communicationService: CommunicationService) {

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
                        Thread {
                            GenericSqsService(connectionService, communicationService)
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
