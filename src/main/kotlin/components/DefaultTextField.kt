package components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import commons.DefaultColors.lightBlue

@Composable
fun defaultTextField(
    modifier: Modifier = Modifier,
    text: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    Column(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(start = 3.dp),
            style = TextStyle(fontSize = 13.sp),
            color = lightBlue
        )
        BasicTextField(
            value = value,
            enabled = enabled,
            textStyle = TextStyle(color = Color.White),
            cursorBrush = SolidColor(Color.Gray),
            singleLine = true,
            onValueChange = onValueChange,
            decorationBox = { innerTextField ->
                Column(
                    modifier
                        .fillMaxWidth()
                        .border(0.5.dp, color = lightBlue, shape = RoundedCornerShape(5.dp))
                        .height(30.dp)
                        .padding(5.dp)
                ) {
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun defaultTextEditor(
    modifier: Modifier = Modifier,
    text: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(start = 3.dp),
            style = TextStyle(fontSize = 13.sp),
            color = lightBlue
        )
        BasicTextField(
            value = value,
            textStyle = TextStyle(color = Color.White),
            cursorBrush = SolidColor(Color.Gray),
            singleLine = false,
            onValueChange = onValueChange,
            decorationBox = { innerTextField ->
                Column(
                    modifier
                        .fillMaxWidth()
                        .border(0.5.dp, color = lightBlue, shape = RoundedCornerShape(5.dp))
                        .height(30.dp)
                        .padding(5.dp)
                ) {
                    innerTextField()
                }
            }
        )
    }
}
