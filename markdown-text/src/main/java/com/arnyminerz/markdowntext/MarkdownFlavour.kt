package com.arnyminerz.markdowntext

import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor

enum class MarkdownFlavour(val descriptor: MarkdownFlavourDescriptor) {
    CommonMark(CommonMarkFlavourDescriptor()),
    Github(GFMFlavourDescriptor()),
}
