import com.arnyminerz.markdowntext.component.CodeFence
import com.arnyminerz.markdowntext.component.ext.isInstanceOf
import com.arnyminerz.markdowntext.processor.JetbrainsMarkdownProcessor
import com.arnyminerz.markdowntext.processor.ProcessingContext
import com.wakaztahir.codeeditor.model.CodeLang
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import utils.buildASTNode
import utils.emptyProcessingContext

class TestCodeFence {
    @Test
    fun `test CodeFence_instanceOf`() {
        assertTrue(
            CodeFence.isInstanceOf(
                emptyProcessingContext,
                buildASTNode(CodeFence.name)
            )
        )
    }

    private fun buildCodeFenceNode(lang: String?, lines: List<String>): CodeFence {
        val allFileText = "```${lang ?: ""}\n${lines.joinToString("\n")}\n```"

        var lastOffset: Int
        val node = buildASTNode(
            type = "CODE_FENCE",
            // Adding 3 for the ``` of the beginning and 3 for the ``` of the end
            // Adding 1 extra to lang for its line break
            // Adding 1 extra to each line for line breaks
            endOffset = 3 +
                (lang?.length?.plus(1) ?: 0) +
                lines.sumOf { it.length + 1 } +
                3,
            children = listOfNotNull(
                buildASTNode(
                    type = "CODE_FENCE_START",
                    endOffset = (3).also { lastOffset = it }
                ),
                lang?.let { l ->
                    buildASTNode(
                        type = "FENCE_LANG",
                        startOffset = lastOffset,
                        endOffset = (lastOffset + l.length).also { lastOffset = it }
                    )
                },
                buildASTNode(
                    type = "EOL",
                    startOffset = lastOffset,
                    endOffset = (lastOffset + 1).also { lastOffset = it }
                ),
                *lines.flatMap { line ->
                    listOf(
                        buildASTNode(
                            type = "CODE_FENCE_CONTENT",
                            startOffset = lastOffset,
                            endOffset = (lastOffset + line.length).also { lastOffset = it }
                        ),
                        buildASTNode(
                            type = "EOL",
                            startOffset = lastOffset,
                            endOffset = (lastOffset + 1).also { lastOffset = it }
                        ),
                    )
                }.toTypedArray(),
                buildASTNode(
                    type = "CODE_FENCE_END",
                    endOffset = (3).also { lastOffset = it }
                ),
            )
        )
        return with(ProcessingContext.build(allFileText, JetbrainsMarkdownProcessor())) {
            with(CodeFence) { extract(node) }
        }
    }

    @Test
    fun `test CodeFence_extract`() {
        buildCodeFenceNode(
            lang = null,
            lines = listOf(
                "Code block without",
                "any language"
            )
        ).let { fence ->
            assertNull(fence.lang)
            assertNull(fence.language)
            assertTrue(fence.lines.isNotEmpty())
            assertEquals(2, fence.lines.size)
            println("lines: ${fence.lines}")
            assertEquals("Code block without", fence.lines[0])
            assertEquals("any language", fence.lines[1])
        }

        buildCodeFenceNode(
            // this name is not official in the library, will be replaced by aliases
            lang = "kotlin",
            lines = listOf(
                "Code block with",
                "Kotlin language"
            )
        ).let { fence ->
            // Note that the alias is replaced by the official name
            assertEquals("kt", fence.lang)
            assertEquals(CodeLang.Kotlin, fence.language)
            assertTrue(fence.lines.isNotEmpty())
            assertEquals(2, fence.lines.size)
            println("lines: ${fence.lines}")
            assertEquals("Code block with", fence.lines[0])
            assertEquals("Kotlin language", fence.lines[1])
        }

        buildCodeFenceNode(
            // this name is not official in the library, will be replaced by aliases
            lang = "cpp",
            lines = listOf(
                "Code block with",
                "C++ language"
            )
        ).let { fence ->
            assertEquals("cpp", fence.lang)
            assertEquals(CodeLang.CPP, fence.language)
            assertTrue(fence.lines.isNotEmpty())
            assertEquals(2, fence.lines.size)
            println("lines: ${fence.lines}")
            assertEquals("Code block with", fence.lines[0])
            assertEquals("C++ language", fence.lines[1])
        }
    }
}
