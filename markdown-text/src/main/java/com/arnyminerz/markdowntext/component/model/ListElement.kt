package com.arnyminerz.markdowntext.component.model

import com.arnyminerz.markdowntext.component.Paragraph

data class ListElement(
    val paragraph: Paragraph,
    val subList: List<ListElement>? = null,
    val prefix: String? = null
): Feature
