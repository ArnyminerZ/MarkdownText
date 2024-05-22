package com.arnyminerz.markdowntext.render

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString.Builder
import com.arnyminerz.markdowntext.component.model.Feature
import com.arnyminerz.markdowntext.MarkdownContainer
import com.arnyminerz.markdowntext.MarkdownText

interface IRenderer<FeatureType : Feature> {
    /**
     * Renders the content of the feature as a standalone component.
     *
     * **Used by [MarkdownContainer]**
     * @param feature The feature to render.
     * @param modifier The modifier to apply to the component.
     */
    @Composable
    fun Content(feature: FeatureType, modifier: Modifier)

    /**
     * Provides a way to append the feature to the [annotatedStringBuilder].
     *
     * Used for instances where the feature can be rendered directly inside of a Text component.
     *
     * **Used by [MarkdownText]**
     * @param annotatedStringBuilder The [Builder] to append the feature to.
     * @param feature The feature to append.
     * @return The [Builder] with the appended feature.
     */
    @Composable
    fun append(annotatedStringBuilder: Builder, feature: FeatureType): Builder
}
