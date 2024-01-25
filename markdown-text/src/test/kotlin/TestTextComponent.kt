import com.arnyminerz.markdowntext.component.model.TextComponent
import com.arnyminerz.markdowntext.component.model.TextComponent.StyledText
import com.arnyminerz.markdowntext.component.model.TextComponent.StyledText.Companion.extract
import com.arnyminerz.markdowntext.processor.ProcessingContext
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode
import utils.assert.assertIsStyledText
import utils.buildASTNode
import kotlin.test.Test
import kotlin.test.assertTrue

class TestTextComponent {
    @Test
    fun `test StyledText_isInstanceOf`() {
        assertTrue(
            StyledText.isInstanceOf(
                buildASTNode(IElementType(StyledText.NODE_BOLD))
            )
        )

        assertTrue(
            StyledText.isInstanceOf(
                buildASTNode(IElementType(StyledText.NODE_ITAL))
            )
        )

        assertTrue(
            StyledText.isInstanceOf(
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
            with(ProcessingContext.build(raw)) {
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
}
