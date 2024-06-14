import com.arnyminerz.markdowntext.component.ext.isInstanceOf
import com.arnyminerz.markdowntext.component.ext.trimStartWS
import com.arnyminerz.markdowntext.component.model.TextComponent
import com.arnyminerz.markdowntext.component.model.TextComponent.StyledText
import com.arnyminerz.markdowntext.component.model.TextComponent.StyledText.Companion.extract
import com.arnyminerz.markdowntext.processor.JetbrainsMarkdownProcessor
import com.arnyminerz.markdowntext.processor.ProcessingContext
import org.intellij.markdown.IElementType
import utils.assert.assertIsStyledText
import utils.buildASTNode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import utils.emptyProcessingContext

class TestTextComponent {
    @Test
    fun `test StyledText_isInstanceOf`() {
        assertTrue(
            StyledText.isInstanceOf(
                emptyProcessingContext,
                buildASTNode(IElementType(StyledText.NODE_BOLD))
            )
        )

        assertTrue(
            StyledText.isInstanceOf(
                emptyProcessingContext,
                buildASTNode(IElementType(StyledText.NODE_ITAL))
            )
        )

        assertTrue(
            StyledText.isInstanceOf(
                emptyProcessingContext,
                buildASTNode(IElementType(StyledText.NODE_STRI))
            )
        )
    }

    @Test
    fun `test StyledText_extract`() {
        fun buildNodeAndExtract(
            pad: String,
            nodeType: String,
            block: (StyledText) -> Unit
        ) {
            val raw = pad + "value" + pad
            val node = buildASTNode(
                type = IElementType(nodeType),
                endOffset = raw.length,
                children = listOf(
                    buildASTNode(
                        type = IElementType("TEXT"),
                        startOffset = pad.length,
                        endOffset = raw.length - pad.length
                    ),
                )
            )
            with(ProcessingContext.build(raw, JetbrainsMarkdownProcessor())) {
                val text = extract(node)
                block(text)
            }
        }

        buildNodeAndExtract("**", StyledText.NODE_BOLD) { text ->
            assertIsStyledText(
                text,
                text = "value",
                isBold = true,
                isItalic = false,
                isStrikethrough = false
            )
        }
        buildNodeAndExtract("_", StyledText.NODE_ITAL) { text ->
            assertIsStyledText(
                text,
                text = "value",
                isBold = false,
                isItalic = true,
                isStrikethrough = false
            )
        }
        buildNodeAndExtract("~~", StyledText.NODE_STRI) { text ->
            assertIsStyledText(
                text,
                text = "value",
                isBold = false,
                isItalic = false,
                isStrikethrough = true
            )
        }
    }

    @Test
    fun `test List-TextComponent_trimStartWS`() {
        val list = listOf(
            TextComponent.WS,
            TextComponent.WS,
            TextComponent.Text("Sample Text")
        ).trimStartWS()
        assertEquals(1, list.size)
        assertEquals("Sample Text", list[0].text)
    }
}
