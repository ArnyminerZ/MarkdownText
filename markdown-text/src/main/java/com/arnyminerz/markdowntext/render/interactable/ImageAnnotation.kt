package com.arnyminerz.markdowntext.render.interactable

internal data class ImageAnnotation(
    val link: String,
    val alt: String,
    val fullWidth: Boolean,
) {
    companion object {
        fun checkbox(checked: Boolean, alt: String) = ImageAnnotation(
            if (checked) "checkbox_checked" else "checkbox",
            alt,
            false,
        )

        fun checkbox(checked: Boolean, alt: CharSequence) = checkbox(checked, alt.toString())
    }

    constructor(link: CharSequence, alt: CharSequence, fullWidth: Boolean) : this(
        link.toString(),
        alt.toString(),
        fullWidth,
    )
}
