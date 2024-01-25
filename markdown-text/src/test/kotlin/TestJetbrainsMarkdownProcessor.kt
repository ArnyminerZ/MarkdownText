import com.arnyminerz.markdowntext.MarkdownFlavour
import com.arnyminerz.markdowntext.component.Paragraph
import com.arnyminerz.markdowntext.component.UnorderedList
import com.arnyminerz.markdowntext.component.model.TextComponent
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
    fun `test load text with line breaks (Github)`() {
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
    fun `test load text with styles (Github)`() {
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

    @Test
    fun `test load unordered list (Github)`() {
        val result = githubProcessor.load(
            """
            - Item 1
            - Item 2
            - Item 3
            - Item 4
            """.trimIndent()
        )
        assertEquals(1, result.size)
        result[0].let { component ->
            // Make sure the component is an UnorderedList
            assertIs<UnorderedList>(component)
            // Check that the list has 4 items
            assertEquals(4, component.list.size)

            fun assertItem(index: Int, text: String) {
                component.list[index].let { paragraph ->
                    assertEquals(1, paragraph.list.size)
                    assertIsText(paragraph.list[0], text)
                }
            }

            // Make sure the texts have been loaded correctly
            assertItem(0, "Item 1")
            assertItem(1, "Item 2")
            assertItem(2, "Item 3")
            assertItem(3, "Item 4")
        }
    }

    @Test
    fun `test load unordered list with styles (Github)`() {
        val result = githubProcessor.load(
            """
            - First item
            - **Bold** item
            - Item with *italics*.
            """.trimIndent()
        )
        assertEquals(1, result.size)
        result[0].let { component ->
            // Make sure the component is an UnorderedList
            assertIs<UnorderedList>(component)
            // Check that the list has 3 items
            assertEquals(3, component.list.size)

            fun assertTextItem(index: Int, text: String) {
                component.list[index].let { paragraph ->
                    assertEquals(1, paragraph.list.size)
                    assertIsText(paragraph.list[0], text)
                }
            }

            // Make sure the texts have been loaded correctly
            assertTextItem(0, "First item")
            component.list[0].list.let { components ->
                assertEquals(1, components.size)
                assertIsText(components[0], "First item")
            }
            component.list[1].list.let { components ->
                assertEquals(3, components.size)
                assertIsStyledText(components[0], "Bold", isBold = true)
                assertIsWS(components[1])
                assertIsText(components[2], "item")
            }
            component.list[2].list.let { components ->
                assertEquals(4, components.size)
                assertIsText(components[0], "Item with")
                assertIsWS(components[1])
                assertIsStyledText(components[2], "italics", isItalic = true)
                assertIsText(components[3], ".")
            }
        }
    }
}
