package com.arnyminerz.markdowntext.render

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle

data class AnnotationStyle(
    val headlineDepthStyles: List<TextStyle>,
    val codeBlockStyle: SpanStyle,
    val linkStyle: SpanStyle,
    val bullet: Char,
    val keepMarkers: Boolean = false,
)
