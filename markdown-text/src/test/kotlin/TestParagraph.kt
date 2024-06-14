import com.arnyminerz.markdowntext.component.Paragraph
import com.arnyminerz.markdowntext.component.flatten
import com.arnyminerz.markdowntext.component.model.TextComponent
import com.arnyminerz.markdowntext.component.model.TextComponent.Text
import com.arnyminerz.markdowntext.component.model.TextComponent.WS
import org.junit.Test
import kotlin.test.assertContentEquals

class TestParagraph {
    @Test
    fun `test Paragraph_add`() {
        val comp1 = Text("this")
        val comp2 = WS
        val comp3 = Text("is")
        val comp4 = WS
        val comp5 = Text("some")
        val comp6 = WS
        val comp7 = Text("testing")
        val comp8 = TextComponent.EOL
        val comp9 = Text("text")
        val par1 = Paragraph(
            listOf(comp1, comp2, comp3, comp4)
        )
        val par2 = Paragraph(
            listOf(comp5, comp6)
        )
        val par3 = Paragraph(
            listOf(comp7, comp8, comp9)
        )
        assertContentEquals(
            listOf(comp1, comp2, comp3, comp4, comp5, comp6, comp7, comp8, comp9),
            (par1 + par2 + par3).list
        )
        assertContentEquals(
            listOf(comp1, comp2, comp3, comp4, comp7, comp8, comp9),
            (par1 + par3).list
        )
    }

    @Test
    fun `test List-Paragraph_flatten`() {
        val comp1 = Text("this")
        val comp2 = WS
        val comp3 = Text("is")
        val comp4 = WS
        val comp5 = Text("some")
        val comp6 = WS
        val comp7 = Text("testing")
        val comp8 = TextComponent.EOL
        val comp9 = Text("text")
        val par1 = Paragraph(
            listOf(comp1, comp2, comp3, comp4)
        )
        val par2 = Paragraph(
            listOf(comp5, comp6, comp7, comp8)
        )
        val par3 = Paragraph(
            listOf(comp9)
        )
        val paragraphs = listOf(
            listOf(par1, par2),
            listOf(par3)
        )
        assertContentEquals(
            listOf(par1 + par2, par3),
            paragraphs.flatten()
        )
    }

    @Test
    fun `test Paragraph_trimStartWS`() {
        val text = Text("Text")

        assertContentEquals(
            listOf(text),
            Paragraph(
                listOf(WS, WS, WS, text)
            ).trimStartWS().list
        )
        assertContentEquals(
            listOf(text),
            Paragraph(
                listOf(text)
            ).trimStartWS().list
        )
        assertContentEquals(
            emptyList(),
            Paragraph(
                listOf(WS)
            ).trimStartWS().list
        )
        assertContentEquals(
            emptyList(),
            Paragraph(
                emptyList()
            ).trimStartWS().list
        )
    }
}
