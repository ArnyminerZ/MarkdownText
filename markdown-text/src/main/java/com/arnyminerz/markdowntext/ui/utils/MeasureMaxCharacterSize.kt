package com.arnyminerz.markdowntext.ui.utils

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize

@Composable
fun rememberMaxCharacterSize(style: TextStyle = LocalTextStyle.current): IntSize {
    val textMeasurer = rememberTextMeasurer()
    return remember(textMeasurer) { textMeasurer.measure("0", style).size }
}
