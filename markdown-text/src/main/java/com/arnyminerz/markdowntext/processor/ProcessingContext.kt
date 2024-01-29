package com.arnyminerz.markdowntext.processor

import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

interface ProcessingContext {
    companion object {
        fun build(allFileText: CharSequence, processor: IProcessor): ProcessingContext =
            object : ProcessingContext {
                override val processor: IProcessor = processor
                override val allFileText: CharSequence = allFileText
            }
    }

    val processor: IProcessor

    val allFileText: CharSequence

    fun ASTNode.getTextInNode(): String {
        return getTextInNode(allFileText).toString()
    }
}
