package com.arnyminerz.markdowntext.annotatedstring

import android.util.Log
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.arnyminerz.markdowntext.*
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

internal fun Iterable<ASTNode>.explode(
    source: String,
    builder: AnnotatedString.Builder,
    annotationStyle: AnnotationStyle,
    images: List<Pair<String, String>> = emptyList(),
    depth: Int = 0,
) = map { it.explode(source, builder, annotationStyle, images, depth) }.flatten()

@OptIn(ExperimentalTextApi::class)
internal fun ASTNode.explode(
    source: String,
    builder: AnnotatedString.Builder,
    annotationStyle: AnnotationStyle,
    images: List<Pair<String, String>> = emptyList(),
    depth: Int = 0,
): List<Pair<String, String>> {
    Log.i(
        "ASG",
        "${"  ".repeat(depth)}- Type: $name \t\t\t Children: ${children.size}. Parent: ${parent?.name}"
    )
    val mutableImages = images.toMutableList()

    when {
        name == "INLINE_LINK" && !hasParentWithName("IMAGE") && !containsNodeWithName("IMAGE") ->
            builder.withStyle(annotationStyle.linkStyle) {
                val text = findChildOfType("LINK_TEXT")
                val link = findChildOfType("LINK_DESTINATION")
                if (text == null || link == null)
                    Log.w("Analyzer", "Malformed tag.")
                else {
                    val url = link.getTextInNode(source).toString()
                    Log.v("Analyzer", "Adding link \"$url\"")
                    withAnnotation(
                        tag = "link",
                        annotation = url,
                    ) { append(text.getNodeLinkText(source).toString()) }
                }
            }
        parent?.name == "IMAGE" -> if (name == "INLINE_LINK") {
            val textNode = findChildOfType("LINK_TEXT")
            val linkNode = findChildOfType("LINK_DESTINATION")
            if (textNode == null || linkNode == null) return mutableImages

            val text = textNode.getNodeLinkText(source).toString()
            val link = linkNode.getTextInNode(source).toString()

            Log.v("Analyzer", "Adding image: $link")
            val parentLink = findParentWithName("INLINE_LINK")
            val imageLinkNode = parentLink?.findChildOfType("LINK_DESTINATION")
            if (imageLinkNode != null) {
                Log.v("Analyzer", "Image has link")
                val imageLink = imageLinkNode.getTextInNode(source).toString()
                builder.withAnnotation(
                    tag = "link",
                    annotation = imageLink,
                ) { appendInlineContent(id = link, alternateText = text) }
            } else
                builder.appendInlineContent(id = link, alternateText = text)
            mutableImages.add(link to text)
        }
        isNotEmpty() -> {
            if (name == "STRONG")
                return builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    children.explode(source, this, annotationStyle, mutableImages, depth + 1)
                }
            if (name == "EMPH")
                return builder.withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    children.explode(source, this, annotationStyle, mutableImages, depth + 1)
                }
            if (name == "CODE_SPAN")
                return builder.withStyle(annotationStyle.codeBlockStyle) {
                    children.explode(source, this, annotationStyle, mutableImages, depth + 1)
                }
            if (name.startsWith("ATX_"))
                name.indexOf('_')
                    .takeIf { it >= 0 }
                    ?.let { name.substring(it + 1).toIntOrNull() }
                    ?.let { annotationStyle.headlineDepthStyles[it - 1] }
                    ?.let {
                        return builder.withStyle(it.toSpanStyle()) {
                            children.explode(
                                source,
                                this,
                                annotationStyle,
                                mutableImages,
                                depth + 1
                            )
                        }
                    }
            return children.explode(source, builder, annotationStyle, mutableImages, depth + 1)
        }
        // Collect all contents of links that leak
        parent?.name == "LINK_DESTINATION" -> return mutableImages
        name == "TEXT" -> builder.append(getTextInNode(source).toString())
        name == "WHITE_SPACE" -> builder.append(' ')
        name == "!" -> builder.append('!')
        name == ":" -> builder.append(':')
        name == "EOL" -> builder.append('\n')
        name == "LIST_BULLET" -> builder.append("${annotationStyle.bullet}\t")
        name == "LIST_NUMBER" -> builder.append("${getTextInNode(source)}\t")
        name == "HORIZONTAL_RULE" -> builder.withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
            append(" ".repeat(50))
        }
    }

    return mutableImages
}
