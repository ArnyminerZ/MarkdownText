package com.arnyminerz.markdowntext.render

import androidx.compose.ui.text.AnnotatedString

abstract class IRendererAppendCallback {
    open fun afterEOL(builder: AnnotatedString.Builder) {}
    open fun afterWS(builder: AnnotatedString.Builder) {}
    open fun afterText(builder: AnnotatedString.Builder) {}
    open fun afterCodeSpan(builder: AnnotatedString.Builder) {}
    open fun afterSingleCharacter(builder: AnnotatedString.Builder) {}
    open fun afterStyledText(builder: AnnotatedString.Builder) {}
}
