package com.arnyminerz.markdowntext.render.code

import androidx.compose.runtime.compositionLocalOf
import com.wakaztahir.codeeditor.prettify.PrettifyParser

val LocalCodeParser = compositionLocalOf { PrettifyParser() }
