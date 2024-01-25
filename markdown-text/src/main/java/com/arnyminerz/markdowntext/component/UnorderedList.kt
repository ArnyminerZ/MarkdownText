package com.arnyminerz.markdowntext.component

import com.arnyminerz.markdowntext.component.model.FeatureCompanion
import com.arnyminerz.markdowntext.component.model.IContainerCompanion
import com.arnyminerz.markdowntext.component.model.IListComponent
import com.arnyminerz.markdowntext.findAllChildrenOfType
import com.arnyminerz.markdowntext.processor.ProcessingContext
import org.intellij.markdown.ast.ASTNode

data class UnorderedList(
    override val list: List<Paragraph>
) : IListComponent {
    companion object : FeatureCompanion, IContainerCompanion<Paragraph, UnorderedList> {
        override val name: String = "UNORDERED_LIST"

        override fun ProcessingContext.explore(root: ASTNode): UnorderedList {
            val paragraphs = root.findAllChildrenOfType("LIST_ITEM")
                // Search all the child LIST_ITEM
                .map { listItem ->
                    // Each LIST_ITEM may have multiple paragraphs
                    listItem
                        .findAllChildrenOfType(Paragraph.name)
                        // Process all paragraphs
                        .map { with(Paragraph) { explore(it) } }
                }
            return UnorderedList(paragraphs.flatten())
        }
    }
}
