package components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import commons.DefaultColors.backgroundColor


@Composable
fun defaultDialog(
    title: String,
    body: String
) {
    Dialog(
        onCloseRequest = {},
        content = {
            Column(
                modifier = Modifier.background(backgroundColor),
            ) {
                Text(title)
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(body, style = MaterialTheme.typography.body2)
                }
            }
        }
    )
}
