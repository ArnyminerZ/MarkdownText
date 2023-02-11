package com.arnyminerz.markdowntext.annotatedstring

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
    }
}
