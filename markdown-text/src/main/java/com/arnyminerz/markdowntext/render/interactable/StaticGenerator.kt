package com.arnyminerz.markdowntext.render.interactable

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.arnyminerz.markdowntext.render.AnnotationStyle
import com.arnyminerz.markdowntext.render.interactable.StaticAnalyzer.explode
import com.arnyminerz.markdowntext.render.model.Generator
import org.intellij.markdown.ast.ASTNode

internal class StaticGenerator(
    source: String,
    tree: ASTNode,
): Generator<Pair<AnnotatedString, List<ImageAnnotation>>>(source, tree) {
    @Composable
    override fun generateAnnotatedString(
        style: AnnotationStyle,
    ): Pair<AnnotatedString, List<ImageAnnotation>> {
        // Log.i("ASG", "There are ${tree.children.size} nodes in the tree.")
        // Log.i("ASG", "Flat children: ${tree.flatChildren().joinToString(", ") { it.name }}")
        // Log.i("ASG", "Has images: ${tree.hasChildWithName("IMAGE")}")
        val builder = AnnotatedString.Builder()
        val images = tree.explode(source, builder, style, emptyList())
        return builder.toAnnotatedString() to images
    }
}
