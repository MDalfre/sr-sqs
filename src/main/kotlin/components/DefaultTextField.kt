package components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import model.Queue

@Composable
fun defaultTextField (
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
            color = Color.Gray
        )
        BasicTextField(
            value = value,
            singleLine = true,
            onValueChange = onValueChange,
            decorationBox = { innerTextField ->
                Column (
                    Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
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
fun defaultDropBox (
    text: String,
    value: String,
    queueValue: List<Queue>,
    onValueChange: (String) -> Unit,
    onClickDropBox: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(
        modifier = Modifier.padding(top = 10.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(start = 3.dp),
            style = TextStyle(fontSize = 13.sp),
            color = Color.Gray
        )
        BasicTextField(
            value = value,
            singleLine = true,
            onValueChange = onValueChange,
            decorationBox = { innerTextField ->
                Column (
                    Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
                        .height(30.dp)
                        .padding(5.dp)
                ) {
                    Icon(icon, "contentDescription", Modifier.clickable { expanded = !expanded })
                    innerTextField()
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            queueValue.forEach { queueName ->
                DropdownMenuItem(onClick = onClickDropBox) {
                    Text(text = queueName.name)
                }
            }
        }
    }
}