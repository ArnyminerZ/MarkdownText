package com.arnyminerz.markdowntext.component

import com.arnyminerz.markdowntext.component.model.FeatureCompanion
import com.arnyminerz.markdowntext.component.model.IContainerCompanion
import com.arnyminerz.markdowntext.component.model.IListComponent
import com.arnyminerz.markdowntext.component.model.ListElement
import com.arnyminerz.markdowntext.findChildOfType
import com.arnyminerz.markdowntext.name
import com.arnyminerz.markdowntext.processor.ProcessingContext
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

data class OrderedList(
    override val list: List<ListElement>
) : IListComponent {
    companion object : FeatureCompanion, IContainerCompanion<ListElement, OrderedList> {
        override val name: String = "ORDERED_LIST"

        override fun ProcessingContext.explore(root: ASTNode): OrderedList {
            val elements = root.children.filter { it.name == "LIST_ITEM" }
                // Search all the child LIST_ITEM
                .mapNotNull { listItem ->
                    val subLists = mutableListOf<ListElement>()
                    listItem.children
                        .filter { it.name == name }
                        .map { with(OrderedList) { explore(it) } }
                        .let { l -> subLists.addAll(l.flatMap { it.list }) }
                    listItem.children
                        .filter { it.name == UnorderedList.name }
                        .map { with(UnorderedList) { explore(it) } }
                        .let { l -> subLists.addAll(l.flatMap { it.list }) }

                    val number = listItem.findChildOfType("LIST_NUMBER")
                        ?.getTextInNode(allFileText)
                        ?.toString()
                        ?.trim()
                        ?: return@mapNotNull null
                    // Each LIST_ITEM may have multiple paragraphs
                    val paragraph = listItem.children
                        .filter { it.name == Paragraph.name }
                        // Process all paragraphs
                        .map { with(Paragraph) { explore(it) } }
                        // Join all paragraphs
                        .join()

                    ListElement(paragraph, subLists, number)
                }
            return OrderedList(elements)
        }
    }
}
