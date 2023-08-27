package com.arnyminerz.markdowntext.render.model

import androidx.compose.runtime.Composable
import com.arnyminerz.markdowntext.render.AnnotationStyle
import org.intellij.markdown.ast.ASTNode

abstract class Generator<Result>(
    protected val source: String,
    protected val tree: ASTNode
) {
    @Composable
    abstract fun generateAnnotatedString(
        style: AnnotationStyle,
    ): Result
}
