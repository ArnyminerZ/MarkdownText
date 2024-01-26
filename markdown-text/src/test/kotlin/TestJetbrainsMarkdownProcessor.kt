import com.arnyminerz.markdowntext.MarkdownFlavour
import com.arnyminerz.markdowntext.component.Header
import com.arnyminerz.markdowntext.component.OrderedList
import com.arnyminerz.markdowntext.component.Paragraph
import com.arnyminerz.markdowntext.component.UnorderedList
import com.arnyminerz.markdowntext.component.model.ListElement
import com.arnyminerz.markdowntext.component.model.TextComponent
import com.arnyminerz.markdowntext.processor.JetbrainsMarkdownProcessor
import utils.assert.assertIsCodeSpan
import utils.assert.assertIsStyledText
import utils.assert.assertIsText
import utils.assert.assertIsWS
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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
    fun `test load text with code span (Github)`() {
        val result = githubProcessor.load("Testing text `with code`")

        // Make sure a single component has been loaded
        assertEquals(1, result.size)
        result[0].let { component ->
            // Make sure the component is a paragraph
            assertIs<Paragraph>(component)
            // Check that the paragraph has 2 components
            assertEquals(3, component.list.size)
            // And that the text is fine
            assertIsText(component.list[0], "Testing text")
            assertIsWS(component.list[1])
            assertIsCodeSpan(component.list[2], "with code")
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
                component.list[index].let { element ->
                    val paragraph = element.paragraph
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

            // Make sure the texts have been loaded correctly
            component.list[0].paragraph.list.let { components ->
                assertEquals(1, components.size)
                assertIsText(components[0], "First item")
            }
            component.list[1].paragraph.list.let { components ->
                assertEquals(3, components.size)
                assertIsStyledText(components[0], "Bold", isBold = true)
                assertIsWS(components[1])
                assertIsText(components[2], "item")
            }
            component.list[2].paragraph.list.let { components ->
                assertEquals(4, components.size)
                assertIsText(components[0], "Item with")
                assertIsWS(components[1])
                assertIsStyledText(components[2], "italics", isItalic = true)
                assertIsText(components[3], ".")
            }
        }
    }

    @Test
    fun `test load unordered nested list (Github)`() {
        val result = githubProcessor.load(
            """
            - Item 1
              - Sub-item 1
              - Sub-item 2
              - Sub-item 3
            """.trimIndent()
        )
        assertEquals(1, result.size)
        result[0].let { component ->
            // Make sure the component is an UnorderedList
            assertIs<UnorderedList>(component)
            // Check that the list has 4 items
            assertEquals(1, component.list.size)

            // Make sure the texts have been loaded correctly
            component.list[0].let { (paragraph, subList, prefix) ->
                assertEquals(ListElement.bullet, prefix)
                assertNotNull(subList)
                assertEquals(3, subList.size)
                subList[0].paragraph.let {
                    assertEquals(1, it.list.size)
                    assertIsText(it.list[0], "Sub-item 1")
                }
                subList[1].paragraph.let {
                    assertEquals(1, it.list.size)
                    assertIsText(it.list[0], "Sub-item 2")
                }
                subList[2].paragraph.let {
                    assertEquals(1, it.list.size)
                    assertIsText(it.list[0], "Sub-item 3")
                }

                assertEquals(1, paragraph.list.size)
                assertIsText(paragraph.list[0], "Item 1")
            }
        }
    }

    @Test
    fun `test load ordered list (Github)`() {
        val result = githubProcessor.load(
            """
            1. Item
            2. Item
            3. Item
            4. Item
            """.trimIndent()
        )
        assertEquals(1, result.size)
        result[0].let { component ->
            // Make sure the component is an UnorderedList
            assertIs<OrderedList>(component)
            // Check that the list has 4 items
            assertEquals(4, component.list.size)

            fun assertItem(index: Int, number: String, text: String) {
                component.list[index].let { (paragraph, _, num) ->
                    assertEquals(number, num)
                    assertEquals(1, paragraph.list.size)
                    assertIsText(paragraph.list[0], text)
                }
            }

            // Make sure the texts have been loaded correctly
            assertItem(0, "1.", "Item")
            assertItem(1, "2.", "Item")
            assertItem(2, "3.", "Item")
            assertItem(3, "4.", "Item")
        }
    }

    @Test
    fun `test load ordered list with styles (Github)`() {
        val result = githubProcessor.load(
            """
            1. First item
            2. **Bold** item
            3. Item with *italics*.
            """.trimIndent()
        )
        assertEquals(1, result.size)
        result[0].let { component ->
            // Make sure the component is an UnorderedList
            assertIs<OrderedList>(component)
            // Check that the list has 3 items
            assertEquals(3, component.list.size)
            val components = component.list.toList()

            // Make sure the texts have been loaded correctly
            components[0].let { element ->
                assertEquals("1.", element.prefix)
                assertEquals(1, element.paragraph.list.size)
                assertIsText(element.paragraph.list[0], "First item")
            }
            components[1].let { (paragraph, _, number) ->
                assertEquals("2.", number)
                assertEquals(3, paragraph.list.size)
                assertIsStyledText(paragraph.list[0], "Bold", isBold = true)
                assertIsWS(paragraph.list[1])
                assertIsText(paragraph.list[2], "item")
            }
            components[2].let { (paragraph, _, number) ->
                assertEquals("3.", number)
                assertEquals(4, paragraph.list.size)
                assertIsText(paragraph.list[0], "Item with")
                assertIsWS(paragraph.list[1])
                assertIsStyledText(paragraph.list[2], "italics", isItalic = true)
                assertIsText(paragraph.list[3], ".")
            }
        }
    }

    @Test
    fun `test load ordered nested list (Github)`() {
        val result = githubProcessor.load(
            """
            1. Item 1
                1. Sub-item 1
                2. Sub-item 2
                3. Sub-item 3
            """.trimIndent()
        )
        assertEquals(1, result.size)
        result[0].let { component ->
            // Make sure the component is an UnorderedList
            assertIs<OrderedList>(component)
            // Check that the list has 4 items
            assertEquals(1, component.list.size)

            // Make sure the texts have been loaded correctly
            component.list[0].let { (paragraph, subList, prefix) ->
                assertEquals("1.", prefix)
                assertNotNull(subList)
                assertEquals(3, subList.size)
                subList[0].let {
                    assertEquals("1.", it.prefix)
                    assertEquals(1, it.paragraph.list.size)
                    assertIsText(it.paragraph.list[0], "Sub-item 1")
                }
                subList[1].let {
                    assertEquals("2.", it.prefix)
                    assertEquals(1, it.paragraph.list.size)
                    assertIsText(it.paragraph.list[0], "Sub-item 2")
                }
                subList[2].let {
                    assertEquals("3.", it.prefix)
                    assertEquals(1, it.paragraph.list.size)
                    assertIsText(it.paragraph.list[0], "Sub-item 3")
                }

                assertEquals(1, paragraph.list.size)
                assertIsText(paragraph.list[0], "Item 1")
            }
        }
    }

    @Test
    fun `test load text with headers (Github)`() {
        val result = githubProcessor.load(
            """
            # Header 1
            ## Header 2
            ### Header 3
            #### Header 4
            ##### Header 5
            ###### Header 6
            """.trimIndent()
        )

        // Make sure 6 components has been loaded
        assertEquals(6, result.size)
        result[0].let { component ->
            // Make sure the component is a paragraph
            assertIs<Header.Header1>(component)
            assertEquals(1, component.list.size)
            assertEquals("Header 1", component.list[0].text)
        }
        result[1].let { component ->
            // Make sure the component is a paragraph
            assertIs<Header.Header2>(component)
            assertEquals(1, component.list.size)
            assertEquals("Header 2", component.list[0].text)
        }
        result[2].let { component ->
            // Make sure the component is a paragraph
            assertIs<Header.Header3>(component)
            assertEquals(1, component.list.size)
            assertEquals("Header 3", component.list[0].text)
        }
        result[3].let { component ->
            // Make sure the component is a paragraph
            assertIs<Header.Header4>(component)
            assertEquals(1, component.list.size)
            assertEquals("Header 4", component.list[0].text)
        }
        result[4].let { component ->
            // Make sure the component is a paragraph
            assertIs<Header.Header5>(component)
            assertEquals(1, component.list.size)
            assertEquals("Header 5", component.list[0].text)
        }
        result[5].let { component ->
            // Make sure the component is a paragraph
            assertIs<Header.Header6>(component)
            assertEquals(1, component.list.size)
            assertEquals("Header 6", component.list[0].text)
        }
    }
}
