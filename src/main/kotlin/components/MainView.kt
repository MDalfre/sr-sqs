package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.ui.Theme
import service.CommunicationService
import service.VariableStore

const val ALERT_WIDTH = 700
const val ALERT_HEIGHT = 500
const val ALERT_SMALL_HEIGHT = 250

@Composable
@Preview
fun mainView(
    communicationService: CommunicationService,
    variableStore: VariableStore,
    theme: Theme
) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 25.dp)
            .width(300.dp)
    ) {
        connectionForm(variableStore = variableStore, communicationService = communicationService, theme = theme)
        mockModeSwitch(variableStore = variableStore)
        systemLogPanel(communicationService = communicationService)
    }

    if (!variableStore.mockMode) {
        sendMessageFrom(variableStore = variableStore, communicationService = communicationService, theme = theme)
        receiveMessageForm(variableStore = variableStore, communicationService = communicationService, theme = theme)
    } else {
        mockMode(variableStore = variableStore, communicationService = communicationService, theme = theme)
    }
}
