import com.arnyminerz.markdowntext.component.Header
import com.arnyminerz.markdowntext.component.Header.Companion.extract
import com.arnyminerz.markdowntext.component.ext.isInstanceOf
import com.arnyminerz.markdowntext.processor.JetbrainsMarkdownProcessor
import com.arnyminerz.markdowntext.processor.ProcessingContext
import org.intellij.markdown.IElementType
import utils.buildASTNode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import utils.emptyProcessingContext

class TestHeader {
    @Test
    fun `test Header_instanceOf`() {
        assertTrue(
            Header.isInstanceOf(
                emptyProcessingContext,
                buildASTNode("ATX_1")
            )
        )
        assertTrue(
            Header.isInstanceOf(
                emptyProcessingContext,
                buildASTNode("ATX_3")
            )
        )
        assertTrue(
            Header.isInstanceOf(
                emptyProcessingContext,
                buildASTNode("ATX_1")
            )
        )
        assertFalse(
            Header.isInstanceOf(
                emptyProcessingContext,
                buildASTNode("ATX_0")
            )
        )
        assertFalse(
            Header.isInstanceOf(
                emptyProcessingContext,
                buildASTNode("ATX_7")
            )
        )
    }

    @Test
    fun `test Header_extract`() {
        fun buildHeaderNode(depth: Int, text: String): Header {
            val allFileText = "#".repeat(depth) + " " + text
            val node = buildASTNode(
                type = "ATX_$depth",
                endOffset = depth + 1 + text.length,
                children = listOf(
                    buildASTNode("ATX_HEADER", endOffset = depth),
                    buildASTNode(
                        type = "ATX_CONTENT",
                        startOffset = depth,
                        endOffset = depth + 1 + text.length,
                        children = listOf(
                            buildASTNode("WHITE_SPACE", startOffset = depth, endOffset = depth + 1),
                            buildASTNode(
                                "TEXT",
                                startOffset = depth + 1,
                                endOffset = depth + 1 + text.length
                            ),
                        )
                    )
                )
            )
            return with(ProcessingContext.build(allFileText, JetbrainsMarkdownProcessor())) {
                with(Header) { extract(node) }
            }
        }
        buildHeaderNode(1, "Header").let { header ->
            assertIs<Header.Header1>(header)
            assertEquals(1, header.list.size)
            assertEquals("Header", header.list[0].text)
        }
        buildHeaderNode(5, "Header").let { header ->
            assertIs<Header.Header5>(header)
            assertEquals(1, header.list.size)
            assertEquals("Header", header.list[0].text)
        }
    }
}
