import com.arnyminerz.markdowntext.MarkdownFlavour
import com.arnyminerz.markdowntext.component.Paragraph
import com.arnyminerz.markdowntext.processor.JetbrainsMarkdownProcessor
import utils.assert.assertIsStyledText
import utils.assert.assertIsText
import utils.assert.assertIsWS
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertIs

class TestJetbrainsMarkdownProcessor {
    private val githubProcessor = JetbrainsMarkdownProcessor(MarkdownFlavour.Github)

    @Test
    fun `test load simple text with line breaks (Github)`() {
        val result = githubProcessor.load("Testing text\nWith paragraphs")

        // Make sure a single component has been loaded
        assertEquals(1, result.size)
        result[0].let { component ->
            // Make sure the component is a paragraph
            assertIs<Paragraph>(component)
            // Check that the paragraph has 3 components
            assertEquals(3, component.list.size)
            // And two lines
            assertEquals(2, component.lines.size)
            // And that the text is fine
            assertEquals("Testing text", component.lines[0])
            assertEquals("With paragraphs", component.lines[1])
        }
    }

    @Test
    fun `test load simple text with styles (Github)`() {
        val result = githubProcessor.load(
            "Text with **bold**, " +
                "_italics_, " +
                "~~strikethrough~~, " +
                "**_bold with italics_**, " +
                "**~~bold with strikethrough~~**, " +
                "_~~italics with strikethrough~~_, " +
                "**_~~bold with italics and strikethrough~~_**."
        )
        assertEquals(1, result.size)
        result[0].let { component ->
            // Make sure the component is a paragraph
            assertIs<Paragraph>(component)
            // Check that the paragraph has 22 components
            assertEquals(22, component.list.size)
            // Check all the individual components
            val list = component.list.iterator()
            assertIsText(list.next(), "Text with")
            assertIsWS(list.next())
            assertIsStyledText(list.next(), "bold", isBold = true)
            assertIsText(list.next(), ",")
            assertIsWS(list.next())
            assertIsStyledText(list.next(), "italics", isItalic = true)
            assertIsText(list.next(), ",")
            assertIsWS(list.next())
            assertIsStyledText(list.next(), "strikethrough", isStrikethrough = true)
            assertIsText(list.next(), ",")
            assertIsWS(list.next())
            assertIsStyledText(
                list.next(),
                "bold with italics",
                isBold = true,
                isItalic = true
            )
            assertIsText(list.next(), ",")
            assertIsWS(list.next())
            assertIsStyledText(
                list.next(),
                "bold with strikethrough",
                isBold = true,
                isStrikethrough = true
            )
            assertIsText(list.next(), ",")
            assertIsWS(list.next())
            assertIsStyledText(
                list.next(),
                "italics with strikethrough",
                isItalic = true,
                isStrikethrough = true
            )
            assertIsText(list.next(), ",")
            assertIsWS(list.next())
            assertIsStyledText(
                list.next(),
                "bold with italics and strikethrough",
                isBold = true,
                isItalic = true,
                isStrikethrough = true
            )
            assertIsText(list.next(), ".")
            assertFalse(list.hasNext())
        }
    }
}
