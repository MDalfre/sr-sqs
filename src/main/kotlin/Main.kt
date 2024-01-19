// Copyright 2000-2021 JetBrains s.r.o. and contributors.
// Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import commons.DefaultColors
import commons.DefaultColors.backgroundColor
import commons.Util.appIcon
import components.mainView
import components.topBar
import model.ui.Theme
import service.CommunicationService
import service.ConnectionService
import service.VariableStore

const val DEFAULT_WIDTH = 1280
const val DEFAULT_HEIGHT = 790
val communicationService: CommunicationService = CommunicationService()
var connectionService: ConnectionService? = null
val variableStore = VariableStore()

@Composable
@Preview
fun app() {
    val theme = Theme(
        buttonModifier = Modifier.padding(10.dp),
        defaultButtonColor = ButtonDefaults.buttonColors(
            backgroundColor = DefaultColors.buttonColor,
            contentColor = Color.Black
        )
    )
    MaterialTheme {
        Row(
            Modifier.background(backgroundColor)
        ) {
            mainView(communicationService, variableStore, theme)
        }
        Column {
            topBar(communicationService, variableStore)
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SR SQS",
        icon = appIcon(),
        state = rememberWindowState(
            width = DEFAULT_WIDTH.dp,
            height = DEFAULT_HEIGHT.dp,
            position = WindowPosition(
                Alignment.Center
            )
        )
    ) { app() }
}

