package com.arnyminerz.markdowntext

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.arnyminerz.markdowntext.annotatedstring.AnnotationStyle
import com.arnyminerz.markdowntext.annotatedstring.explode
import org.intellij.markdown.ast.ASTNode

class AnnotatedStringGenerator(
    private val source: String,
    private val tree: ASTNode,
) {
    @Composable
    fun generateAnnotatedString(
        style: AnnotationStyle,
    ): Pair<AnnotatedString, List<Pair<String, String>>> {
        Log.i("ASG", "There are ${tree.children.size}.")
        val builder = AnnotatedString.Builder()
        val images = tree.explode(source, builder, style)
        return builder.toAnnotatedString() to images
    }
}
