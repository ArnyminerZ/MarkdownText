package com.arnyminerz.markdowntext.render.code

import androidx.compose.runtime.compositionLocalOf
import com.wakaztahir.codeeditor.highlight.prettify.PrettifyParser

val LocalCodeParser = compositionLocalOf { PrettifyParser() }
