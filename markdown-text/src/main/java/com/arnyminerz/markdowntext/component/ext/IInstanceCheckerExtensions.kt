package com.arnyminerz.markdowntext.component.ext

import com.arnyminerz.markdowntext.component.model.IInstanceChecker
import com.arnyminerz.markdowntext.processor.ProcessingContext

fun <SourceType : Any> IInstanceChecker<SourceType>.isInstanceOf(
    context: ProcessingContext,
    instance: SourceType
): Boolean = with(context) { isInstanceOf(instance) }
