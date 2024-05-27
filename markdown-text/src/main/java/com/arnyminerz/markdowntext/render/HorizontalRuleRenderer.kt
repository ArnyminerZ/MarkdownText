package com.arnyminerz.markdowntext.render

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntSize
import com.arnyminerz.markdowntext.component.HorizontalRule

object HorizontalRuleRenderer : IRenderer<HorizontalRule> {
    @Composable
    override fun LazyItemScope.Content(feature: HorizontalRule, modifier: Modifier) {
        HorizontalDivider(modifier)
    }

    context(RenderContext)
    @Composable
    override fun append(feature: HorizontalRule) {
        annotatedStringBuilder.appendLine("----")
    }
}
