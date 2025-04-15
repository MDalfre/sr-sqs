package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.amazonaws.regions.Regions
import commons.Constants.MATERIAL_ICON_DIMENSION
import commons.DefaultColors
import commons.ImageHelper
import communicationService
import connectionService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.ConnectionSettings
import model.CredentialType
import model.ui.Theme
import service.ConnectionService
import service.FileHandleService
import service.GenericSqsService
import variableStore

@OptIn(DelicateCoroutinesApi::class)
@Suppress("LongMethod", "ComplexMethod")
@Composable
@Preview
fun ConnectionForm(theme: Theme) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.background(DefaultColors.backgroundColor).fillMaxSize()
    ) {
        Image(
            modifier = Modifier.size(200.dp),
            painter = remember { BitmapPainter(ImageHelper().loadImageBitmap("logo2.png")) },
            contentScale = ContentScale.Fit,
            contentDescription = "Sr-SqS",
            alignment = Alignment.BottomStart,
        )
        Spacer(modifier = Modifier.size(MATERIAL_ICON_DIMENSION.dp))
        CredentialTypeDropDown()
        DefaultTextField(
            modifier = Modifier.width(300.dp),
            text = "Server URL",
            value = variableStore.serverUrl,
            onValueChange = { variableStore.serverUrl = it }
        )
        DefaultTextField(
            modifier = Modifier.width(300.dp),
            text = "Access Key",
            value = variableStore.accessKey,
            onValueChange = { variableStore.accessKey = it }
        )
        DefaultTextField(
            modifier = Modifier.width(300.dp),
            text = "Secret Key",
            value = variableStore.secretKey,
            onValueChange = { variableStore.secretKey = it }
        )
        if (variableStore.selectedCredential == CredentialType.SESSION.name) {
            DefaultTextField(
                modifier = Modifier.width(300.dp),
                text = "Session Key",
                value = variableStore.sessionKey,
                onValueChange = { variableStore.sessionKey = it }
            )
        }
        RegionDropDown()
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

                GlobalScope.launch {
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
                }
            }) {
            Text("Connect")
        }
        Button(
            enabled = variableStore.connecting,
            colors = theme.defaultButtonColor,
            modifier = theme.buttonModifier,
            onClick = {
                GlobalScope.launch {
                    variableStore.connecting = false
                    connectionService!!.disconnect()
                }
            }) {
            Text("Disconnect")
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CredentialTypeDropDown() {
    Text(
        text = "Credential Type",
        color = DefaultColors.secondaryColor,
        style = TextStyle(fontFamily = FontFamily.Monospace),
        modifier = Modifier.padding(bottom = 5.dp)
    )
    ExposedDropdownMenuBox(
        expanded = variableStore.expandedCredential,
        onExpandedChange = { variableStore.expandedCredential = !variableStore.expandedCredential },
    ) {
        BasicTextField(
            value = variableStore.selectedCredential,
            readOnly = true,
            textStyle = LocalTextStyle.current.copy(color = DefaultColors.tintColor, textAlign = TextAlign.Center),
            onValueChange = {},
            decorationBox = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(300.dp)
                        .border(0.5.dp, color = DefaultColors.secondaryColor, shape = RoundedCornerShape(5.dp))
                        .height(30.dp)
                        .padding(5.dp)
                ) {
                    it()
                }
            }
        )
        ExposedDropdownMenu(
            expanded = variableStore.expandedCredential,
            onDismissRequest = { variableStore.expandedCredential = !variableStore.expandedCredential }) {
            CredentialType.entries.forEach {
                DropdownMenuItem(
                    onClick = {
                        variableStore.selectedCredential = it.name
                        variableStore.expandedCredential = !variableStore.expandedCredential
                    }
                ) {
                    Text(
                        fontFamily = FontFamily.Monospace,
                        text = it.name
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RegionDropDown() {
    Text(
        text = "Region",
        color = DefaultColors.secondaryColor,
        style = TextStyle(fontFamily = FontFamily.Monospace),
        modifier = Modifier.padding(bottom = 5.dp)
    )
    ExposedDropdownMenuBox(
        expanded = variableStore.expandedRegion,
        onExpandedChange = { variableStore.expandedRegion = !variableStore.expandedRegion },
    ) {
        BasicTextField(
            value = variableStore.selectedRegion.toString(),
            readOnly = true,
            textStyle = LocalTextStyle.current.copy(color = DefaultColors.tintColor, textAlign = TextAlign.Center),
            onValueChange = {},
            decorationBox = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(300.dp)
                        .border(0.5.dp, color = DefaultColors.secondaryColor, shape = RoundedCornerShape(5.dp))
                        .height(30.dp)
                        .padding(5.dp)
                ) {
                    it()
                }
            }
        )
        ExposedDropdownMenu(
            variableStore.expandedRegion,
            onDismissRequest = { variableStore.expandedRegion = !variableStore.expandedRegion }) {
            Regions.entries.forEach {
                DropdownMenuItem(
                    onClick = {
                        println(variableStore.selectedRegion)
                        variableStore.selectedRegion = it.name
                        variableStore.expandedRegion = !variableStore.expandedRegion
                    }
                ) {
                    Text(
                        fontFamily = FontFamily.Monospace,
                        text = it.name
                    )
                }
            }
        }
    }
}
