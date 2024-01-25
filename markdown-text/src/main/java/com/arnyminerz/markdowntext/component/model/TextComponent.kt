package com.arnyminerz.markdowntext.component.model

open class TextComponent private constructor(
    val text: String
) : IComponent {
    class EOL : TextComponent("\n") {
        companion object: FeatureCompanion {
            override val name: String = "EOL"
        }
    }

    class Text(text: String) : TextComponent(text) {
        companion object: FeatureCompanion {
            override val name: String = "TEXT"
        }
    }

    override fun toString(): String = text
}
