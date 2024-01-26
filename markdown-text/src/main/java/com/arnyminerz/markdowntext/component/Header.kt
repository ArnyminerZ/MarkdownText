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

        override fun isInstanceOf(node: ASTNode): Boolean {
            if (!node.name.startsWith("ATX_", true)) return false
            val index = node.name.substringAfter("ATX_").toIntOrNull()
            return index != null && (1..6).contains(index)
        }

        override fun ProcessingContext.extract(node: ASTNode): Header {
            val content = node.findChildOfType(HEADER_CONTENT)!!
            val list = with(Paragraph) { explore(content) }.list.trimStartWS()
            return when (node.name.substringAfter("ATX_").toIntOrNull()) {
                1 -> Header1(list)
                2 -> Header2(list)
                3 -> Header3(list)
                4 -> Header4(list)
                5 -> Header5(list)
                6 -> Header6(list)
                else -> error("Got an invalid header: ${node.name}")
            }
        }
    }

    class Header1(list: List<TextComponent>) : Header(0, list) {
        companion object : FeatureCompanion {
            override val name: String = "ATX_1"
        }
    }

    class Header2(list: List<TextComponent>) : Header(1, list) {
        companion object : FeatureCompanion {
            override val name: String = "ATX_2"
        }
    }

    class Header3(list: List<TextComponent>) : Header(2, list) {
        companion object : FeatureCompanion {
            override val name: String = "ATX_3"
        }
    }

    class Header4(list: List<TextComponent>) : Header(3, list) {
        companion object : FeatureCompanion {
            override val name: String = "ATX_4"
        }
    }

    class Header5(list: List<TextComponent>) : Header(4, list) {
        companion object : FeatureCompanion {
            override val name: String = "ATX_5"
        }
    }

    class Header6(list: List<TextComponent>) : Header(5, list) {
        companion object : FeatureCompanion {
            override val name: String = "ATX_6"
        }
    }
}
