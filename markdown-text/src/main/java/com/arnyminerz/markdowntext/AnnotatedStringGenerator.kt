package com.arnyminerz.markdowntext

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.arnyminerz.markdowntext.annotatedstring.AnnotationStyle
import com.arnyminerz.markdowntext.annotatedstring.ImageAnnotation
import com.arnyminerz.markdowntext.annotatedstring.explode
import org.intellij.markdown.ast.ASTNode

internal class AnnotatedStringGenerator(
    private val source: String,
    private val tree: ASTNode,
) {
    @Composable
    fun generateAnnotatedString(
        style: AnnotationStyle,
    ): Pair<AnnotatedString, List<ImageAnnotation>> {
        // Log.i("ASG", "There are ${tree.children.size} nodes in the tree.")
        // Log.i("ASG", "Flat children: ${tree.flatChildren().joinToString(", ") { it.name }}")
        // Log.i("ASG", "Has images: ${tree.hasChildWithName("IMAGE")}")
        val builder = AnnotatedString.Builder()
        val images = tree.explode(source, builder, style)
        return builder.toAnnotatedString() to images
    }
}
