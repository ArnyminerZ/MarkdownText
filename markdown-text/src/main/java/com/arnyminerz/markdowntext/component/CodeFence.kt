package com.arnyminerz.markdowntext.component

import com.arnyminerz.markdowntext.component.model.FeatureCompanion
import com.arnyminerz.markdowntext.component.model.IComponent
import com.arnyminerz.markdowntext.component.model.NodeExtractor
import com.arnyminerz.markdowntext.component.model.NodeTypeCheck
import com.arnyminerz.markdowntext.component.model.TextComponent
import com.arnyminerz.markdowntext.findAllChildrenOfType
import com.arnyminerz.markdowntext.findChildOfType
import com.arnyminerz.markdowntext.hasChildWithName
import com.arnyminerz.markdowntext.name
import com.arnyminerz.markdowntext.processor.ProcessingContext
import com.wakaztahir.codeeditor.highlight.model.CodeLang
import org.intellij.markdown.ast.ASTNode

data class CodeFence(
    val lang: String?,
    val lines: List<String>
) : IComponent {
    companion object : FeatureCompanion, NodeTypeCheck, NodeExtractor<CodeFence> {
        override val name: String = "CODE_FENCE"

        private const val FENCE_LANG: String = "FENCE_LANG"

        private const val CODE_FENCE_CONTENT: String = "CODE_FENCE_CONTENT"

        override fun ProcessingContext.isInstanceOf(instance: ASTNode): Boolean {
            return instance.name == name && instance.hasChildWithName(TextComponent.Text.name)
        }

        override fun ProcessingContext.extract(node: ASTNode): CodeFence {
            val lang = node.findChildOfType(FENCE_LANG)
            val lines = node.findAllChildrenOfType(CODE_FENCE_CONTENT)
            return CodeFence(
                lang = lang?.getTextInNode(),
                lines = lines.map { it.getTextInNode() }
            )
        }
    }

    val language: CodeLang? = CodeLang.entries.find { item ->
        val acceptedNames = item.value
        acceptedNames.find { it.equals(lang, true) } != null
    }
}
