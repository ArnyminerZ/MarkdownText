package com.arnyminerz.markdowntext.processor

interface ProcessingContext {
    companion object {
        fun build(allFileText: CharSequence): ProcessingContext = object : ProcessingContext {
            override val allFileText: CharSequence = allFileText
        }
    }

    val allFileText: CharSequence
}
