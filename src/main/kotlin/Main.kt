// Copyright 2000-2021 JetBrains s.r.o. and contributors.
// Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import commons.DefaultColors.backgroundColor
import commons.Util.appIcon
import components.mainView
import components.topBar
import service.CommunicationService
import service.ConnectionService
import service.FileHandleService

const val DEFAULT_WIDTH = 1280
const val DEFAULT_HEIGHT = 790
val communicationService: CommunicationService = CommunicationService()
var connectionService: ConnectionService? = null

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
    ) {
        val configHandler = FileHandleService().readConfigFile()

        MaterialTheme {
            Row(
                Modifier.background(backgroundColor)
            ) {
                mainView(communicationService, configHandler)
            }
            Column {
                topBar(communicationService)
            }
        }
    }

}

