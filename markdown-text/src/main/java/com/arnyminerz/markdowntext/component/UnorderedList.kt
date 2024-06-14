package com.arnyminerz.markdowntext.component

import com.arnyminerz.markdowntext.component.model.FeatureCompanion
import com.arnyminerz.markdowntext.component.model.IContainerCompanion
import com.arnyminerz.markdowntext.component.model.IListComponent
import com.arnyminerz.markdowntext.component.model.ListElement
import com.arnyminerz.markdowntext.component.model.ListElement.Companion.bullet
import com.arnyminerz.markdowntext.component.model.TextComponent
import com.arnyminerz.markdowntext.name
import com.arnyminerz.markdowntext.processor.ProcessingContext
import org.intellij.markdown.ast.ASTNode

data class UnorderedList(
    override val list: List<ListElement>
) : IListComponent {
    companion object : FeatureCompanion, IContainerCompanion<ListElement, UnorderedList> {
        override val name: String = "UNORDERED_LIST"

        override fun ProcessingContext.explore(root: ASTNode): UnorderedList {
            val elements = root.children.filter { it.name == "LIST_ITEM" }
                // Search all the child LIST_ITEM
                .map { listItem ->
                    val subLists = mutableListOf<ListElement>()
                    listItem.children
                        .filter { it.name == OrderedList.name }
                        .map { with(OrderedList) { explore(it) } }
                        .let { l -> subLists.addAll(l.flatMap { it.list }) }
                    listItem.children
                        .filter { it.name == name }
                        .map { with(UnorderedList) { explore(it) } }
                        .let { l -> subLists.addAll(l.flatMap { it.list }) }

                    // Each LIST_ITEM may have multiple paragraphs
                    val paragraph = listItem.children
                        .filter { it.name == Paragraph.name }
                        // Process all paragraphs
                        .map { with(Paragraph) { explore(it) } }
                        .join()
                        .trimStartWS()

                    val checkbox = paragraph.list
                        .find { it is TextComponent.Checkbox } as TextComponent.Checkbox?

                    ListElement(
                        paragraph,
                        subLists,
                        prefix = if (checkbox != null) {
                            if (checkbox.isChecked) "☑" else "☐"
                        } else {
                            bullet
                        }
                    )
                }
            return UnorderedList(elements)
        }
    }
}
