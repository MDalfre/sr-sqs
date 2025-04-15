package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import commons.DefaultSizes.DEFAULT_PADDING_TOP
import communicationService
import model.ui.Theme
import variableStore

const val ALERT_WIDTH = 700
const val ALERT_HEIGHT = 500
const val ALERT_SMALL_HEIGHT = 250

@Composable
@Preview
fun MainView(theme: Theme) {
    if (!communicationService.sqsConnected && !variableStore.connecting) {
        ConnectionForm(theme)
    } else {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = DEFAULT_PADDING_TOP)
                .width(300.dp)
        ) {
            MockModeSwitch()
            SystemLogPanel()
        }
        if (!variableStore.mockMode) {
            SendMessageFrom(theme)
            ReceiveMessageForm(theme)
        } else {
            MockMode(theme)
        }
    }
}
