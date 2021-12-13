// Copyright 2000-2021 JetBrains s.r.o. and contributors.
// Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import commons.backgroundBlue
import components.mainView
import components.topBar
import service.ConnectionService
import service.LogService

const val DEFAULT_WIDTH = 1280
const val DEFAULT_HEIGHT = 790
val logService: LogService = LogService()
var connectionService: ConnectionService? = null

fun main() = Window(
    title = "SR SQS",
    size = IntSize(DEFAULT_WIDTH, DEFAULT_HEIGHT),
    resizable = true
) {
    MaterialTheme {
        Row(
            Modifier.background(backgroundBlue)
        ) {
            mainView(logService)
        }
        Column {
            topBar(logService)
        }
    }
}

