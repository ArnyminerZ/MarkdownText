package com.arnyminerz.markdowntext.component

import com.arnyminerz.markdowntext.component.model.FeatureCompanion
import com.arnyminerz.markdowntext.component.model.IContainerCompanion
import com.arnyminerz.markdowntext.component.model.TextComponent
import com.arnyminerz.markdowntext.component.model.TextComponent.EOL
import com.arnyminerz.markdowntext.component.model.TextComponent.StyledText
import com.arnyminerz.markdowntext.component.model.TextComponent.Text
import com.arnyminerz.markdowntext.component.model.TextComponent.WS
import com.arnyminerz.markdowntext.component.model.ITextContainer
import com.arnyminerz.markdowntext.component.model.TextComponent.StyledText.Companion.extract
import com.arnyminerz.markdowntext.name
import com.arnyminerz.markdowntext.processor.ProcessingContext
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

data class Paragraph(
    override val list: List<TextComponent>
) : ITextContainer {
    companion object : FeatureCompanion, IContainerCompanion<TextComponent, Paragraph> {
        override val name: String = "PARAGRAPH"

        override fun ProcessingContext.explore(root: ASTNode): Paragraph {
            val list = mutableListOf<TextComponent>()
            for (node in root.children) {
                val component = when {
                    node.name == Text.name -> Text(
                        text = node.getTextInNode(allFileText).toString()
                    )
                    node.name == EOL.name -> EOL()
                    node.name == WS.name -> WS()
                    StyledText.isInstanceOf(node) -> with(StyledText) { extract(node) }
                    else -> error("Got an invalid component: ${node.name}")
                }
                list.add(component)
            }
            return Paragraph(list)
        }
    }

    val lines: List<String> = list.joinToString("") { it.text }.split('\n')

    override fun toString(): String = list.joinToString("") { it.toString() }
}
