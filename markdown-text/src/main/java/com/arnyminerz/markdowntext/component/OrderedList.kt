package com.arnyminerz.markdowntext.component

import com.arnyminerz.markdowntext.component.model.FeatureCompanion
import com.arnyminerz.markdowntext.component.model.IContainerCompanion
import com.arnyminerz.markdowntext.component.model.IListComponent
import com.arnyminerz.markdowntext.component.model.IMapComponent
import com.arnyminerz.markdowntext.component.model.IMapComponentCompanion
import com.arnyminerz.markdowntext.findAllChildrenOfType
import com.arnyminerz.markdowntext.findChildOfType
import com.arnyminerz.markdowntext.processor.ProcessingContext
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

data class OrderedList(
    override val list: Map<String, Paragraph>
) : IMapComponent<String, Paragraph> {
    companion object : FeatureCompanion, IMapComponentCompanion<String, Paragraph, OrderedList> {
        override val name: String = "ORDERED_LIST"

        override fun ProcessingContext.explore(root: ASTNode): OrderedList {
            val paragraphs = root.findAllChildrenOfType("LIST_ITEM")
                // Search all the child LIST_ITEM
                .mapNotNull { listItem ->
                    val number = listItem.findChildOfType("LIST_NUMBER")
                        ?.getTextInNode(allFileText)
                        ?.toString()
                        ?: return@mapNotNull null
                    // Each LIST_ITEM may have multiple paragraphs
                    number to listItem
                        .findAllChildrenOfType(Paragraph.name)
                        // Process all paragraphs
                        .map { with(Paragraph) { explore(it) } }
                }
                .toMap()
                .mapValues { it.value.join() }
            return OrderedList(paragraphs)
        }
    }
}
