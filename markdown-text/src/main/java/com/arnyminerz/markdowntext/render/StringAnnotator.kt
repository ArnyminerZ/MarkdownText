package com.arnyminerz.markdowntext.render

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.buildAnnotatedString
import com.arnyminerz.markdowntext.component.Header
import com.arnyminerz.markdowntext.component.OrderedList
import com.arnyminerz.markdowntext.component.Paragraph
import com.arnyminerz.markdowntext.component.UnorderedList
import com.arnyminerz.markdowntext.component.model.Feature

@Composable
@ExperimentalTextApi
fun buildAnnotatedString(
    features: List<Feature>
) = buildAnnotatedString {
    for (feature in features) {
        when (feature) {
            is Paragraph -> ParagraphRenderer().append(this, feature)
            is OrderedList -> ListRenderer.append(this, feature)
            is UnorderedList -> ListRenderer.append(this, feature)
            is Header -> HeaderRenderer.append(this, feature)
            else -> Log.e("MarkdownText", "Got unknown feature: ${feature::class.simpleName}")
        }
    }
}
