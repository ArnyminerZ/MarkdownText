package com.arnyminerz.markdowntext.component.model

import com.arnyminerz.markdowntext.processor.ProcessingContext
import org.intellij.markdown.ast.ASTNode

interface NodeExtractor<Type : IComponent> {
    fun ProcessingContext.extract(node: ASTNode): Type
}
