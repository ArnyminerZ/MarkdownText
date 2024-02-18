package com.arnyminerz.markdowntext.component.ext

import com.arnyminerz.markdowntext.component.model.TextComponent

fun <Component: TextComponent> List<Component>.trimStartWS(): List<Component> {
    val startIndex = indexOfFirst { it !is TextComponent.WS }
    return subList(startIndex, size)
}
