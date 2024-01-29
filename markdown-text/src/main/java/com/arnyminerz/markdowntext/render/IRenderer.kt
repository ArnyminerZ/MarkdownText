package com.arnyminerz.markdowntext.render

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString.Builder
import com.arnyminerz.markdowntext.component.model.Feature

interface IRenderer<FeatureType : Feature> {
    @Composable
    fun Content(feature: FeatureType)

    @Composable
    fun append(annotatedStringBuilder: Builder, feature: FeatureType): Builder
}