package com.arnyminerz.markdowntext.processor

interface ProcessingContext {
    companion object {
        fun build(allFileText: CharSequence, processor: IProcessor): ProcessingContext =
            object : ProcessingContext {
                override val processor: IProcessor = processor
                override val allFileText: CharSequence = allFileText
            }
    }

    val processor: IProcessor

    val allFileText: CharSequence
}
