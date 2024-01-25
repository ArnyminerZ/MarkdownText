import com.arnyminerz.markdowntext.MarkdownFlavour
import com.arnyminerz.markdowntext.component.Paragraph
import com.arnyminerz.markdowntext.processor.JetbrainsMarkdownProcessor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TestJetbrainsMarkdownProcessor {
    private val githubProcessor = JetbrainsMarkdownProcessor(MarkdownFlavour.Github)

    @Test
    fun `test load simple text with line breaks (Github)`() {
        val result = githubProcessor.load("Testing text\nWith paragraphs")
        println(result)
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
}
