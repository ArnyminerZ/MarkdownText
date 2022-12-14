package com.arnyminerz.markdowntext

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
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
    ): AnnotatedString = buildAnnotatedString {
        Log.i("ASG", "There are ${tree.children.size}.")
        tree.explode(source, this, style)
    }
}
