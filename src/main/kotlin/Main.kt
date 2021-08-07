// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntSize
import components.mainView
import service.ConnectionService
import service.GenericProducerService
const val DEFAULT_WIDTH = 1290
const val DEFAULT_HEIGHT = 930

fun main() = Window(
    title = "SR SQS",
    size = IntSize(DEFAULT_WIDTH, DEFAULT_HEIGHT),
    resizable = false
) {
    MaterialTheme {
        Row {

            mainView()
        }
    }
}

