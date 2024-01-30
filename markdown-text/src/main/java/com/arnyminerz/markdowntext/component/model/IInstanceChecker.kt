package com.arnyminerz.markdowntext.component.model

interface IInstanceChecker<SourceType : Any> {
    fun isInstanceOf(instance: SourceType): Boolean
}
