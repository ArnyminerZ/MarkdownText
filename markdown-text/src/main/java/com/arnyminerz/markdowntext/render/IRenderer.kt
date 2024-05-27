package com.arnyminerz.markdowntext.render

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString.Builder
import com.arnyminerz.markdowntext.MarkdownContainer
import com.arnyminerz.markdowntext.MarkdownText
import com.arnyminerz.markdowntext.component.model.Feature

interface IRenderer<FeatureType : Feature> {
    /**
     * Renders the content of the feature as a standalone component.
     *
     * **Used by [MarkdownContainer]**
     * @param feature The feature to render.
     * @param modifier The modifier to apply to the component.
     */
    @Composable
    fun LazyItemScope.Content(feature: FeatureType, modifier: Modifier)

    /**
     * Provides a way to append the feature to the string builder in the current context.
     *
     * Used for instances where the feature can be rendered directly inside of a Text component.
     *
     * **Used by [MarkdownText]**
     * @param feature The feature to append.
     * @return The [Builder] with the appended feature.
     */
    context(RenderContext)
    @Composable
    fun append(feature: FeatureType)
}
