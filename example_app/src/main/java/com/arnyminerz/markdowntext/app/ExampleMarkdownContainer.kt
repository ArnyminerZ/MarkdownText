package com.arnyminerz.markdowntext.app

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.tooling.preview.Preview
import com.arnyminerz.markdowntext.MarkdownContainer
import com.arnyminerz.markdowntext.MarkdownFlavour
import com.arnyminerz.markdowntext.processor.JetbrainsMarkdownProcessor

@Preview(showBackground = true, showSystemUi = true)
@Composable
@OptIn(ExperimentalTextApi::class)
fun ExampleMarkdownContainer(
    modifier: Modifier = Modifier,
    flavour: MarkdownFlavour = MarkdownFlavour.CommonMark,
) {
    ProvideTextStyle(
        value = LocalTextStyle.current.copy(color = Color.Red)
    ) {
        MarkdownContainer(
            markdown = exampleMarkdown,
            processor = JetbrainsMarkdownProcessor(flavour),
            modifier = modifier
        )
    }
}
