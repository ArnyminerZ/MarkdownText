import com.arnyminerz.markdowntext.component.Paragraph
import com.arnyminerz.markdowntext.component.flatten
import com.arnyminerz.markdowntext.component.model.TextComponent
import org.junit.Test
import kotlin.test.assertContentEquals

class TestParagraph {
    @Test
    fun `test Paragraph_add`() {
        val comp1 = TextComponent.Text("this")
        val comp2 = TextComponent.WS
        val comp3 = TextComponent.Text("is")
        val comp4 = TextComponent.WS
        val comp5 = TextComponent.Text("some")
        val comp6 = TextComponent.WS
        val comp7 = TextComponent.Text("testing")
        val comp8 = TextComponent.EOL
        val comp9 = TextComponent.Text("text")
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
        val comp1 = TextComponent.Text("this")
        val comp2 = TextComponent.WS
        val comp3 = TextComponent.Text("is")
        val comp4 = TextComponent.WS
        val comp5 = TextComponent.Text("some")
        val comp6 = TextComponent.WS
        val comp7 = TextComponent.Text("testing")
        val comp8 = TextComponent.EOL
        val comp9 = TextComponent.Text("text")
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
}
