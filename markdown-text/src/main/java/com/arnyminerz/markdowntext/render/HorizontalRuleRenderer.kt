package com.arnyminerz.markdowntext.render

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import com.arnyminerz.markdowntext.component.HorizontalRule

object HorizontalRuleRenderer : IRenderer<HorizontalRule> {
    @Composable
    override fun LazyItemScope.Content(feature: HorizontalRule, modifier: Modifier) {
        HorizontalDivider(modifier)
    }

    @Composable
    override fun append(
        annotatedStringBuilder: AnnotatedString.Builder,
        feature: HorizontalRule
    ): AnnotatedString.Builder {
        return annotatedStringBuilder.appendLine("----") as AnnotatedString.Builder
    }
}
