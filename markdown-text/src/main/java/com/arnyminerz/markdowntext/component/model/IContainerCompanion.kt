package com.arnyminerz.markdowntext.component.model

import com.arnyminerz.markdowntext.processor.ProcessingContext
import org.intellij.markdown.ast.ASTNode

/**
 * Specifies the functions to be declared by containers.
 */
interface IContainerCompanion<Type : Feature, Container : IContainer<Type>> {
    fun ProcessingContext.explore(root: ASTNode): Container
}
