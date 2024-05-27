package com.arnyminerz.markdowntext

import androidx.compose.ui.unit.IntSize

/**
 * Get the ratio of the size (width / height).
 */
internal fun IntSize.ratio(): Float = width.toFloat() / height.toFloat()
