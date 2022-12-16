package com.arnyminerz.markdowntext.annotatedstring

import androidx.compose.ui.text.AnnotatedString

fun AnnotatedString.Builder.append(charSequence: CharSequence) = append(charSequence.toString())
