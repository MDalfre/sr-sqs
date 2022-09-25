package components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import commons.DefaultColors
import commons.Util
import model.ui.Theme
import service.VariableStore

@Composable
fun versionCheckDialog(variableStore: VariableStore, theme: Theme) {
    val uriHandler = LocalUriHandler.current
    Dialog(
        onCloseRequest = { variableStore.versionDialogOpen = !variableStore.versionDialogOpen },
        title = "New version",
        state = rememberDialogState(size = DpSize(ALERT_SMALL_WIDTH.dp, ALERT_CHECK_VERSION_HEIGHT.dp)),
        icon = Util.appIcon(),
        resizable = false,
        content = {
            Column(
                Modifier.background(DefaultColors.backgroundColor).fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = AnnotatedString(
                            text = "There is a new version of SR-SQS available,please check ",
                            spanStyle = SpanStyle(DefaultColors.tintColor)
                        ).plus(
                            AnnotatedString(
                                text = "https://github.com/MDalfre/sr-sqs/releases ",
                                spanStyle = SpanStyle(DefaultColors.secondaryColor)
                            )
                        ).plus(
                            AnnotatedString(
                                text = "for updates.",
                                spanStyle = SpanStyle(DefaultColors.tintColor)
                            )
                        )
                    )
                }
                Row(
                    Modifier.background(DefaultColors.backgroundColor).fillMaxSize().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        modifier = theme.buttonModifier,
                        enabled = true,
                        colors = theme.defaultButtonColor,
                        onClick = {
                            variableStore.versionDialogOpen = !variableStore.versionDialogOpen
                            uriHandler.openUri("https://github.com/MDalfre/sr-sqs/releases")
                        }
                    ) {
                        Text("Check")
                    }
                }
            }
        }
    )
}
