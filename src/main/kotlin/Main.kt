// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.unit.IntSize
import components.mainView
import service.LogService

const val DEFAULT_WIDTH = 1280
const val DEFAULT_HEIGHT = 800
val logService: LogService = LogService()

fun main() = Window(
    title = "SR SQS",
    size = IntSize(DEFAULT_WIDTH, DEFAULT_HEIGHT),
    resizable = false
) {
    MaterialTheme {
        Row {

            mainView(logService)
        }
    }
}

