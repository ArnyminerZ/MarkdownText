package com.arnyminerz.markdowntext.render.style

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import com.arnyminerz.markdowntext.ui.style.LocalCodeSpanStyle
import com.arnyminerz.markdowntext.ui.style.LocalHeaderStyles
import com.arnyminerz.markdowntext.ui.style.LocalLinkSpanStyle
import com.arnyminerz.markdowntext.ui.style.defaultCodeSpanStyle
import com.arnyminerz.markdowntext.ui.style.defaultLinkSpanStyle

data class TextStyles(
    val codeSpanStyle: SpanStyle,
    val linkSpanStyle: SpanStyle,
    val headerStyles: HeaderStyles
) {
    companion object {
        @Composable
        fun getFromCompositionLocal(): TextStyles {
            return TextStyles(
                codeSpanStyle = LocalCodeSpanStyle.current ?: defaultCodeSpanStyle(),
                linkSpanStyle = LocalLinkSpanStyle.current ?: defaultLinkSpanStyle(),
                headerStyles = LocalHeaderStyles.current ?: HeaderStyles.fromMaterialTheme()
            )
        }
    }
}
