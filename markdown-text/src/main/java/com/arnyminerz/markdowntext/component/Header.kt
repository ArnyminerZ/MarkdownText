package com.arnyminerz.markdowntext.component

import androidx.annotation.IntRange
import com.arnyminerz.markdowntext.component.ext.trimStartWS
import com.arnyminerz.markdowntext.component.model.FeatureCompanion
import com.arnyminerz.markdowntext.component.model.IContainer
import com.arnyminerz.markdowntext.component.model.NodeExtractor
import com.arnyminerz.markdowntext.component.model.NodeTypeCheck
import com.arnyminerz.markdowntext.component.model.TextComponent
import com.arnyminerz.markdowntext.findChildOfType
import com.arnyminerz.markdowntext.name
import com.arnyminerz.markdowntext.processor.ProcessingContext
import org.intellij.markdown.ast.ASTNode

open class Header(
    @IntRange(from = 0, to = 5) val depth: Int,
    override val list: List<TextComponent>
) : IContainer<TextComponent> {
    companion object : NodeTypeCheck, NodeExtractor<Header> {
        private const val HEADER_CONTENT = "ATX_CONTENT"

        private const val MAX_DEPTH = 6

        private val headers: Set<(list: List<TextComponent>) -> Header> = setOf(
            { Header1(it) },
            { Header2(it) },
            { Header3(it) },
            { Header4(it) },
            { Header5(it) },
            { Header6(it) }
        )

        override fun isInstanceOf(node: ASTNode): Boolean {
            if (!node.name.startsWith("ATX_", true)) return false
            val index = node.name.substringAfter("ATX_").toIntOrNull()
            return index != null && (1..MAX_DEPTH).contains(index)
        }

        override fun ProcessingContext.extract(node: ASTNode): Header {
            val content = node.findChildOfType(HEADER_CONTENT)!!
            val list = with(Paragraph) { explore(content) }.list.trimStartWS()
            val depth = node.name.substringAfter("ATX_").toIntOrNull()
            checkNotNull(depth) { "Could not get a valid depth." }
            val headerConstructor = headers.elementAt(depth - 1)
            return headerConstructor(list)
        }
    }

    class Header1(list: List<TextComponent>) : Header(DEPTH_IDX, list) {
        companion object : FeatureCompanion {
            override val name: String = "ATX_1"

            const val DEPTH_IDX = 0
        }
    }

    class Header2(list: List<TextComponent>) : Header(DEPTH_IDX, list) {
        companion object : FeatureCompanion {
            override val name: String = "ATX_2"

            const val DEPTH_IDX = 1
        }
    }

    class Header3(list: List<TextComponent>) : Header(DEPTH_IDX, list) {
        companion object : FeatureCompanion {
            override val name: String = "ATX_3"

            const val DEPTH_IDX = 2
        }
    }

    class Header4(list: List<TextComponent>) : Header(DEPTH_IDX, list) {
        companion object : FeatureCompanion {
            override val name: String = "ATX_4"

            const val DEPTH_IDX = 3
        }
    }

    class Header5(list: List<TextComponent>) : Header(DEPTH_IDX, list) {
        companion object : FeatureCompanion {
            override val name: String = "ATX_5"

            const val DEPTH_IDX = 4
        }
    }

    class Header6(list: List<TextComponent>) : Header(DEPTH_IDX, list) {
        companion object : FeatureCompanion {
            override val name: String = "ATX_6"

            const val DEPTH_IDX = 5
        }
    }
}
