package com.arnyminerz.markdowntext.component.model

import com.arnyminerz.markdowntext.processor.ProcessingContext
import org.intellij.markdown.ast.ASTNode

/**
 * Specifies the functions to be declared by IMapComponents.
 */
interface IMapComponentCompanion<Key : Any, Value : Feature, Component : IMapComponent<Key, Value>> {
    fun ProcessingContext.explore(root: ASTNode): Component
}
