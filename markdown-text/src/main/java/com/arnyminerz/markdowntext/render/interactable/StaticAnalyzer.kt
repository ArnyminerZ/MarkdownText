package com.arnyminerz.markdowntext.render.interactable

import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.arnyminerz.markdowntext.appendInlineContent
import com.arnyminerz.markdowntext.findChildOfType
import com.arnyminerz.markdowntext.findParentWithName
import com.arnyminerz.markdowntext.getNodeLinkText
import com.arnyminerz.markdowntext.hasChildWithName
import com.arnyminerz.markdowntext.hasParentWithName
import com.arnyminerz.markdowntext.isNotEmpty
import com.arnyminerz.markdowntext.name
import com.arnyminerz.markdowntext.render.AnnotationStyle
import com.arnyminerz.markdowntext.render.model.IAnalyzer
import com.arnyminerz.markdowntext.withAnnotation
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

private const val TAG = "ASA"

/**
 * Analyzes Markdown text that will not be modified. Allows showing images and interacting with the
 * rendered elements.
 */
internal object StaticAnalyzer: IAnalyzer<List<ImageAnnotation>> {
    private fun Iterable<ASTNode>.explode(
        source: String,
        builder: AnnotatedString.Builder,
        annotationStyle: AnnotationStyle,
        images: List<ImageAnnotation> = emptyList(),
        depth: Int = 0,
        render: Boolean = true,
    ) = mapIndexed { index, astNode ->
        astNode.explode(
            source,
            builder,
            annotationStyle,
            images,
            this.elementAtOrNull(index - 1),
            depth,
            render
        )
    }.flatten()

    @OptIn(ExperimentalTextApi::class)
    override fun ASTNode.explode(
        source: String,
        builder: AnnotatedString.Builder,
        annotationStyle: AnnotationStyle,
        previous: List<ImageAnnotation>,
        previousNode: ASTNode?,
        depth: Int,
        render: Boolean,
    ): List<ImageAnnotation> {
        Log.i(
            TAG,
            "${"  ".repeat(depth)}- Type: $name \t\t\t Children: ${children.size}. Parent: ${parent?.name}. Previous: ${previousNode?.name}.${
                if (children.isEmpty()) " Value: ${
                    getTextInNode(
                        source
                    )
                }" else ""
            }"
        )
        if (!render)
            return if (isNotEmpty())
                children.explode(source, builder, annotationStyle, emptyList(), depth + 1, false)
            else
                emptyList()
        val mutableImages = previous.toMutableList()

        when {
            name == "TABLE" -> builder.withStyle(annotationStyle.codeBlockStyle) {
                builder.append(getTextInNode(source))
            }
            name == "CODE_BLOCK" -> builder.withStyle(annotationStyle.codeBlockStyle) {
                builder.append(getTextInNode(source))
            }
            name == "INLINE_LINK" && !hasParentWithName("IMAGE") && !hasChildWithName("IMAGE") ->
                builder.withStyle(annotationStyle.linkStyle) {
                    val text = findChildOfType("LINK_TEXT")
                    val link = findChildOfType("LINK_DESTINATION")
                    if (text == null || link == null)
                        Log.w(TAG, "Malformed tag.")
                    else {
                        val url = link.getTextInNode(source).toString()
                        Log.v(TAG, "Adding link \"$url\"")
                        withAnnotation(
                            tag = "link",
                            annotation = url,
                        ) { append(text.getNodeLinkText(source)) }
                    }
                }
            name == "IMAGE" -> if (hasChildWithName("INLINE_LINK")) {
                val textNode = findChildOfType("LINK_TEXT")
                val linkNode = findChildOfType("LINK_DESTINATION")
                if (textNode == null || linkNode == null) return mutableImages

                val text = textNode.getNodeLinkText(source)
                val link = linkNode.getTextInNode(source)

                Log.v(TAG, "Adding image: $link")
                val parentLink = findParentWithName("INLINE_LINK")
                val imageLinkNode = parentLink?.findChildOfType("LINK_DESTINATION")
                if (imageLinkNode != null) {
                    Log.v(TAG, "Image has link")
                    val imageLink = imageLinkNode.getTextInNode(source)
                    builder.withAnnotation(
                        tag = "link",
                        annotation = imageLink,
                    ) { appendInlineContent(id = link, alternateText = text) }
                } else
                    builder.appendInlineContent(id = link, alternateText = text)
                mutableImages.add(ImageAnnotation(link, text, previousNode?.name == "EOL"))
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
                if (name == "STRIKETHROUGH")
                    return builder.withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
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
                            findChildOfType("ATX_CONTENT")
                                ?.getTextInNode(source)
                                ?.trimStart()
                                ?.let { text ->
                                    builder.withStyle(it.toSpanStyle()) { append(text) }
                                }
                            return mutableImages
                        }
                return children.explode(source, builder, annotationStyle, mutableImages, depth + 1)
            }
            // Collect all contents of links that leak
            parent?.name == "LINK_DESTINATION" -> return mutableImages
            name == "TEXT" -> builder.append(getTextInNode(source))
            name == "WHITE_SPACE" -> builder.append(' ')
            name == "!" -> builder.append('!')
            name == ":" -> builder.append(':')
            name == "[" && parent?.name != "LINK_TEXT" -> builder.append('[')
            name == "]" && parent?.name != "LINK_TEXT" -> builder.append(']')
            name == "(" && parent?.name != "INLINE_LINK" -> builder.append('(')
            name == ")" && parent?.name != "INLINE_LINK" -> builder.append(')')
            name == "'" -> builder.append('\'')
            name == "\"" -> builder.append('"')
            name == "EOL" -> builder.append('\n')
            name == "LIST_BULLET" && parent?.hasChildWithName("CHECK_BOX") != true -> builder.append("${annotationStyle.bullet}\t")
            name == "LIST_NUMBER" -> builder.append("${getTextInNode(source)}\t")
            name == "HORIZONTAL_RULE" -> builder.withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                append(" ".repeat(50))
            }
            name == "CHECK_BOX" -> {
                val text = getTextInNode(source)
                val unchecked = text.contains("[ ]")
                builder.appendInlineContent(
                    id = if (unchecked) "checkbox" else "checkbox_checked",
                    alternateText = text,
                )
                mutableImages.add(ImageAnnotation.checkbox(!unchecked, text))
            }
            name == "GFM_AUTOLINK" -> getTextInNode(source).let { link ->
                builder.withStyle(annotationStyle.linkStyle) {
                    withAnnotation(
                        tag = "link",
                        annotation = link,
                    ) { append(link) }
                }
            }
        }

        return mutableImages
    }
}
