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
import androidx.compose.ui.window.v1.Dialog
import commons.backgroundBlue


@Composable
fun defaultDialog(
    title: String,
    body: String
) {
    Dialog(
        onDismissRequest = {},
        content = {
            Column(
                modifier = Modifier.background(backgroundBlue),
            ) {
                Text(title)
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(body, style = MaterialTheme.typography.body2)
                }
            }
        }
    )
}
