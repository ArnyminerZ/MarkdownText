package com.arnyminerz.markdowntext.component.model

import com.arnyminerz.markdowntext.processor.ProcessingContext

interface IInstanceChecker<SourceType : Any> {
    fun ProcessingContext.isInstanceOf(instance: SourceType): Boolean
}
