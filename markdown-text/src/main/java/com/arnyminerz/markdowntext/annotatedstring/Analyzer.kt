package com.arnyminerz.markdowntext.annotatedstring

import android.util.Log
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.arnyminerz.markdowntext.*
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

fun Iterable<ASTNode>.explode(
    source: String,
    builder: AnnotatedString.Builder,
    annotationStyle: AnnotationStyle,
    depth: Int = 0,
) = forEach { it.explode(source, builder, annotationStyle, depth) }

@OptIn(ExperimentalTextApi::class)
fun ASTNode.explode(
    source: String,
    builder: AnnotatedString.Builder,
    annotationStyle: AnnotationStyle,
    depth: Int = 0,
) {
    Log.i(
        "ASG",
        "${"  ".repeat(depth)}- Type: $name \t\t\t Children: ${children.size}. Parent: ${parent?.name}"
    )

    when {
        name == "INLINE_LINK" && parent?.name != "IMAGE" && children.find { it.name == "IMAGE" } == null ->
            builder.withStyle(annotationStyle.linkStyle) {
                val text = findChildOfType("LINK_TEXT")
                val link = findChildOfType("LINK_DESTINATION")
                if (text == null || link == null)
                    Log.w("Analyzer", "Malformed tag.")
                else {
                    val url = link.getTextInNode(source).toString()
                    Log.v("Analyzer", "Adding link \"$url\" from $startOffset to $endOffset")
                    withAnnotation(
                        tag = "link",
                        annotation = url,
                    ) { append(text.getNodeLinkText(source).toString()) }
                }
            }
        name == "IMAGE" -> findChildOfType("INLINE_LINK")?.let { link ->

        }
        isNotEmpty() -> run {
            if (name == "STRONG")
                return@run builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    children.explode(source, this, annotationStyle, depth + 1)
                }
            if (name == "EMPH")
                return@run builder.withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    children.explode(source, this, annotationStyle, depth + 1)
                }
            if (name == "CODE_SPAN")
                return@run builder.withStyle(annotationStyle.codeBlockStyle) {
                    children.explode(source, this, annotationStyle, depth + 1)
                }
            if (name.startsWith("ATX_"))
                name.indexOf('_')
                    .takeIf { it >= 0 }
                    ?.let { name.substring(it + 1).toIntOrNull() }
                    ?.let { annotationStyle.headlineDepthStyles[it - 1] }
                    ?.let {
                        return@run builder.withStyle(it.toSpanStyle()) {
                            children.explode(source, this, annotationStyle, depth + 1)
                        }
                    }
            children.explode(source, builder, annotationStyle, depth + 1)
        }
        name == "TEXT" -> builder.append(getTextInNode(source).toString())
        name == "WHITE_SPACE" -> builder.append(' ')
        name == "!" -> builder.append('!')
        name == "EOL" -> builder.append('\n')
        name == "LIST_BULLET" -> builder.append("${annotationStyle.bullet}\t")
        name == "LIST_NUMBER" -> builder.append("${getTextInNode(source)}\t")
        name == "HORIZONTAL_RULE" -> builder.withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
            append(" ".repeat(50))
        }
    }
}
