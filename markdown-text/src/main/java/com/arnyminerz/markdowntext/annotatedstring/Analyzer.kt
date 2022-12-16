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

private const val TAG = "ASA"

internal fun Iterable<ASTNode>.explode(
    source: String,
    builder: AnnotatedString.Builder,
    annotationStyle: AnnotationStyle,
    images: List<ImageAnnotation> = emptyList(),
    conserveMarkers: Boolean = false,
    depth: Int = 0,
    render: Boolean = true,
) = mapIndexed { index, astNode ->
    astNode.explode(
        source,
        builder,
        annotationStyle,
        images,
        conserveMarkers,
        this.elementAtOrNull(index - 1),
        depth,
        render
    )
}.flatten()

@OptIn(ExperimentalTextApi::class)
internal fun ASTNode.explode(
    source: String,
    builder: AnnotatedString.Builder,
    annotationStyle: AnnotationStyle,
    images: List<ImageAnnotation> = emptyList(),
    conserveMarkers: Boolean = false,
    previousNode: ASTNode? = null,
    depth: Int = 0,
    render: Boolean = true,
): List<ImageAnnotation> = with(builder) {
    Log.i(
        TAG,
        "${"  ".repeat(depth)}- Type: $name \t\t\t Children: ${children.size}. Parent: ${parent?.name}. Previous: ${previousNode?.name}"
    )
    if (!render)
        return if (isNotEmpty())
            children.explode(
                source,
                builder,
                annotationStyle,
                emptyList(),
                conserveMarkers,
                depth + 1,
                false
            )
        else
            emptyList()
    val mutableImages = images.toMutableList()

    fun AnnotatedString.Builder.kaboom() = children.explode(
        source,
        this,
        annotationStyle,
        mutableImages,
        conserveMarkers,
        depth + 1,
    )

    when {
        name == "INLINE_LINK" && !hasParentWithName("IMAGE") && !hasChildWithName("IMAGE") ->
            annotationStyle.linkStyle.style {
                val text = findChildOfType("LINK_TEXT")
                val link = findChildOfType("LINK_DESTINATION")
                if (text == null || link == null)
                    Log.w(TAG, "Malformed tag.")
                else {
                    val url = link.getTextInNode(source)
                    val txt = text.getNodeLinkText(source)
                    Log.v(TAG, "Adding link \"$url\"")
                    with(annotationStyle) { appendLink(url, txt) }
                }
            }
        name == "IMAGE" -> if (hasChildWithName("INLINE_LINK")) {
            val textNode = findChildOfType("LINK_TEXT") ?: return mutableImages
            val linkNode = findChildOfType("LINK_DESTINATION") ?: return mutableImages

            val text = textNode.getNodeLinkText(source)
            val link = linkNode.getTextInNode(source)

            Log.v(TAG, "Adding image: $link")
            val parentLink = findParentWithName("INLINE_LINK")
            val imageLinkNode = parentLink?.findChildOfType("LINK_DESTINATION")
            if (imageLinkNode != null) {
                Log.v(TAG, "Image has link")
                val imageLink = imageLinkNode.getTextInNode(source)
                with(annotationStyle) { appendInlineContentLink(imageLink, link, text) }
            } else
                appendInlineContent(id = link, alternateText = text)
            mutableImages.add(ImageAnnotation(link, text, previousNode?.name == "EOL"))
        }
        isNotEmpty() -> {
            if (name == "STRONG")
                return FontWeight.Bold.span.style { kaboom() }
            if (name == "EMPH")
                return FontStyle.Italic.span.style { kaboom() }
            if (name == "STRIKETHROUGH")
                return TextDecoration.LineThrough.span.style { kaboom() }
            if (name == "CODE_SPAN")
                return annotationStyle.codeBlockStyle.style { kaboom() }
            if (name.startsWith("ATX_"))
                name.indexOfOrNull('_')
                    ?.let { name.substring(it + 1).toIntOrNull() }
                    ?.let { annotationStyle.headlineDepthStyles[it - 1] }
                    ?.let { headerStyle ->
                        var text = findChildOfType("ATX_CONTENT")
                            ?.getTextInNode(source)
                            ?.letIf(!conserveMarkers) { it.trimStart() }
                            ?: return@let

                        if (conserveMarkers)
                            findChildOfType("ATX_HEADER")
                                ?.getTextInNode(source)
                                ?.let { h -> text = h + text }

                        headerStyle.toSpanStyle().style { append(text) }
                        return mutableImages
                    }
            return kaboom()
        }
        // Collect all contents of links that leak
        parent?.name == "LINK_DESTINATION" -> return mutableImages
        name == "TEXT" -> append(getTextInNode(source))
        name == "WHITE_SPACE" -> append(' ')
        name == "!" -> append('!')
        name == ":" -> append(':')
        name == "[" && parent?.name != "LINK_TEXT" -> append('[')
        name == "]" && parent?.name != "LINK_TEXT" -> append(']')
        name == "EOL" -> append('\n')
        name == "LIST_BULLET" && parent?.hasChildWithName("CHECK_BOX") != true -> append("${annotationStyle.bullet}\t")
        name == "LIST_NUMBER" -> append("${getTextInNode(source)}\t")
        name == "HORIZONTAL_RULE" -> TextDecoration.LineThrough.span.style { append(" ".repeat(50)) }
        name == "CHECK_BOX" -> {
            val text = getTextInNode(source).toString()
            val unchecked = text.contains("[ ]")
            appendInlineContent(
                id = with(unchecked) { "checkbox" or "checkbox_checked" },
                alternateText = text,
            )
            mutableImages.add(ImageAnnotation.checkbox(!unchecked, text))
        }
        name == "GFM_AUTOLINK" -> getNodeLinkText(source).let { link ->
            with(annotationStyle) { appendLink(link) }
        }
        // Once reached down here, these indicators should be ignored if conserveMarkers is false
        name == "EMPH" && (conserveMarkers || (parent?.name != "STRONG" && parent?.name != "EMPH")) ->
            append('*')
        name == "~" && (conserveMarkers || parent?.name != "STRIKETHROUGH") ->
            append('~')
        name == "BACKTICK" && (conserveMarkers || parent?.name != "CODE_SPAN") ->
            append('`')
        name == "[" && (conserveMarkers || parent?.name?.contains("LINK") == false) ->
            append('[')
        name == "]" && (conserveMarkers || parent?.name?.contains("LINK") == false) ->
            append(']')
        name == "(" && (conserveMarkers || parent?.name?.contains("LINK") == false) ->
            append('(')
        name == ")" && (conserveMarkers || parent?.name?.contains("LINK") == false) ->
            append(')')
    }

    return mutableImages
}
