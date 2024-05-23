package utils

import com.arnyminerz.markdowntext.processor.JetbrainsMarkdownProcessor
import com.arnyminerz.markdowntext.processor.ProcessingContext

val emptyProcessingContext = ProcessingContext.build("", JetbrainsMarkdownProcessor())
