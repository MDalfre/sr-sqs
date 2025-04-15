package model.ui

import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import commons.DefaultColors

data class Theme(
    val buttonModifier: Modifier,
    val defaultButtonColor: ButtonColors
) {
    @Composable
    fun build() = Theme(
        buttonModifier = Modifier, defaultButtonColor = ButtonDefaults.buttonColors(
            backgroundColor = DefaultColors.buttonColor,
            contentColor = Color.Black
        )
    )
}
