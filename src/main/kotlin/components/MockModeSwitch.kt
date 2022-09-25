package components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import commons.DefaultColors
import service.VariableStore

@Composable
fun mockModeSwitch(
    variableStore: VariableStore
) {
    Row(
        Modifier
            .fillMaxWidth().border(0.5.dp, color = DefaultColors.secondaryColor, shape = RoundedCornerShape(5.dp)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Mock Mode:",
            style = TextStyle(fontSize = 13.sp),
            color = DefaultColors.secondaryColor
        )
        Switch(
            modifier = Modifier.size(30.dp),
            checked = variableStore.mockMode,
            onCheckedChange = { variableStore.mockMode = !variableStore.mockMode }
        )
    }
}
