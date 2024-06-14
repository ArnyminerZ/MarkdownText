package com.arnyminerz.markdowntext.component

import com.arnyminerz.markdowntext.component.model.Feature
import com.arnyminerz.markdowntext.component.model.FeatureCompanion
import com.arnyminerz.markdowntext.component.model.IComponent

data class MarkdownFile(
    val components: List<IComponent>
): Feature {
    companion object : FeatureCompanion {
        override val name: String = "MARKDOWN_FILE"
    }
}
