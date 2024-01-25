@file:Suppress("LongParameterList", "LongMethod", "CyclomaticComplexMethod", "SwallowedException", "FunctionNaming")

package com.arnyminerz.markdowntext.render.model

import androidx.compose.ui.text.AnnotatedString
import com.arnyminerz.markdowntext.render.AnnotationStyle
import org.intellij.markdown.ast.ASTNode

interface IAnalyzer<Result> {
    fun ASTNode.explode(
        source: String,
        builder: AnnotatedString.Builder,
        annotationStyle: AnnotationStyle,
        previous: Result,
        previousNode: ASTNode? = null,
        depth: Int = 0,
        render: Boolean = true,
    ): Result
}
