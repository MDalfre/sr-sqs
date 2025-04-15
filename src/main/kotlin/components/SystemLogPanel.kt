package components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import commons.DefaultColors
import commons.DefaultSizes.LOG_COLUM_SIZE
import commons.Util
import communicationService
import model.LogTypeEnum

@Composable
fun SystemLogPanel() {
    val listStateLog = rememberLazyListState()

    Column(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        Text(
            text = "System log",
            modifier = Modifier.padding(start = 3.dp),
            style = TextStyle(fontSize = 13.sp),
            color = DefaultColors.secondaryColor
        )
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .height(LOG_COLUM_SIZE)
                .border(0.5.dp, color = DefaultColors.secondaryColor, shape = RoundedCornerShape(5.dp)),
            state = listStateLog,
        ) {
            items(communicationService.systemLog.asReversed()) { logMessage ->
                val color = when (logMessage.type) {
                    LogTypeEnum.INFO -> Color.Gray
                    LogTypeEnum.WARN -> Color.Yellow
                    LogTypeEnum.ERROR -> Color.Red
                    LogTypeEnum.SUCCESS -> Color.Green
                }
                Column(
                    Modifier.padding(5.dp)
                ) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(logMessage.message, style = MaterialTheme.typography.caption, color = color)
                    }
                    Divider(color = DefaultColors.secondaryColor)
                }
            }
        }
        Row(
            Modifier.fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Image(
                modifier = Modifier.size(200.dp),
                painter = Util.appLogo(),
                contentDescription = "SR SQS",
                alignment = Alignment.Center
            )
        }
    }
}
