package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amazonaws.regions.Regions
import commons.DefaultColors.dropDownColor
import connectionService
import model.ConnectionSettings
import model.CredentialType
import model.ui.Theme
import service.CommunicationService
import service.ConnectionService
import service.FileHandleService
import service.GenericSqsService
import service.VariableStore

@Suppress("LongMethod", "ComplexMethod")
@Composable
@Preview
fun connectionForm(
    variableStore: VariableStore,
    communicationService: CommunicationService,
    theme: Theme
) {

    Row {
        defaultTextField(
            modifier = Modifier.clickable { variableStore.expandedCredential = !variableStore.expandedCredential },
            text = "Credential Type1",
            value = variableStore.selectedCredential,
            onValueChange = { variableStore.selectedCredential = it }
        )
        DropdownMenu(
            modifier = Modifier.background(dropDownColor),
            expanded = variableStore.expandedCredential,
            onDismissRequest = { variableStore.expandedCredential = !variableStore.expandedCredential }
        ) {
            CredentialType.entries.forEach {
                DropdownMenuItem(
                    modifier = Modifier.padding(5.dp).height(15.dp),
                    onClick = {
                        variableStore.selectedCredential = it.name
                        variableStore.expandedCredential = !variableStore.expandedCredential
                    }
                ) {
                    Text(text = it.name, style = TextStyle(fontSize = 10.sp))
                }
            }
        }
    }
    defaultTextField(
        text = "Server URL",
        value = variableStore.serverUrl,
        onValueChange = { variableStore.serverUrl = it }
    )
    defaultTextField(
        text = "Access Key",
        value = variableStore.accessKey,
        onValueChange = { variableStore.accessKey = it }
    )
    defaultTextField(
        text = "Secret Key",
        value = variableStore.secretKey,
        onValueChange = { variableStore.secretKey = it }
    )
    if (variableStore.selectedCredential == CredentialType.SESSION.name) {
        defaultTextField(
            text = "Session Key",
            value = variableStore.sessionKey,
            onValueChange = { variableStore.sessionKey = it }
        )
    }
    Row {
        defaultTextField(
            modifier = Modifier.clickable { variableStore.expandedRegion = !variableStore.expandedRegion },
            text = "Region",
            value = variableStore.selectedRegion,
            onValueChange = { variableStore.selectedRegion = it }
        )
        DropdownMenu(
            modifier = Modifier.background(dropDownColor),
            expanded = variableStore.expandedRegion,
            onDismissRequest = { variableStore.expandedRegion = !variableStore.expandedRegion }
        ) {
            Regions.entries.forEach {
                DropdownMenuItem(
                    modifier = Modifier.padding(5.dp).height(15.dp),
                    onClick = {
                        variableStore.selectedRegion = it.name
                        variableStore.expandedRegion = !variableStore.expandedRegion
                    }
                ) {
                    Text(text = it.name, style = TextStyle(fontSize = 10.sp))
                }
            }
        }
    }
    Row(
        Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            enabled = !variableStore.connecting,
            colors = theme.defaultButtonColor,
            modifier = theme.buttonModifier,
            onClick = {
                val settings = ConnectionSettings(
                    credentialType = CredentialType.valueOf(variableStore.selectedCredential),
                    serverUrl = variableStore.serverUrl,
                    accessKey = variableStore.accessKey,
                    secretKey = variableStore.secretKey,
                    sessionKey = variableStore.sessionKey,
                    serverRegion = Regions.valueOf(variableStore.selectedRegion)
                )
                FileHandleService().createConfigFile(settings)

                Thread {
                    variableStore.connecting = true
                    connectionService = ConnectionService(
                        connectionSettings = settings,
                        communicationService = communicationService
                    )
                    if (communicationService.sqsConnected) {
                        variableStore.queues = GenericSqsService(
                            connectionService = requireNotNull(connectionService) { "SQS service not connected" },
                            communicationService = communicationService
                        ).getQueues()
                    }
                    variableStore.connecting = communicationService.sqsConnected
                }.start()
            }) {
            Text("Connect")
        }
        Button(
            enabled = variableStore.connecting,
            colors = theme.defaultButtonColor,
            modifier = theme.buttonModifier,
            onClick = {
                Thread {
                    variableStore.connecting = false
                    connectionService!!.disconnect()
                }.start()
            }) {
            Text("Disconnect")
        }
    }
}
