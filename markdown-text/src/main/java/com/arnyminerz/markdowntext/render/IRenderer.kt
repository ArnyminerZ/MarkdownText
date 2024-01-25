package com.arnyminerz.markdowntext.render

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.arnyminerz.markdowntext.component.model.Feature

interface IRenderer<FeatureType : Feature> {
    @Composable
    fun Content(feature: FeatureType)

    fun append(annotatedStringBuilder: AnnotatedString.Builder, feature: FeatureType)
}
