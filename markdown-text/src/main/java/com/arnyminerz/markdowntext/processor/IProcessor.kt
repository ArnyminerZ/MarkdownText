package com.arnyminerz.markdowntext.processor

import com.arnyminerz.markdowntext.component.model.IComponent

interface IProcessor {
    fun load(markdown: String): List<IComponent>
}
