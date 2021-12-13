// Copyright 2000-2021 JetBrains s.r.o. and contributors.
// Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import commons.DefaultColors.backgroundBlue
import components.mainView
import components.topBar
import service.ConnectionService
import service.CommunicationService
import service.FileHandleService

const val DEFAULT_WIDTH = 1280
const val DEFAULT_HEIGHT = 790
val communicationService: CommunicationService = CommunicationService()
var connectionService: ConnectionService? = null

fun main() = Window(
    title = "SR SQS",
    size = IntSize(DEFAULT_WIDTH, DEFAULT_HEIGHT),
    resizable = true
) {
    val configHandler = FileHandleService().readConfigFile()

    MaterialTheme {
        Row(
            Modifier.background(backgroundBlue)
        ) {
            mainView(communicationService, configHandler)
        }
        Column {
            topBar(communicationService)
        }
    }
}

