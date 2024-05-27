package utils.assert

import com.arnyminerz.markdowntext.component.model.TextComponent
import kotlin.test.assertEquals
import kotlin.test.assertIs

fun assertIsText(component: TextComponent, text: String) {
    assertIs<TextComponent.Text>(component)
    assertEquals(text, component.text)
}

fun assertIsWS(component: TextComponent) {
    assertIs<TextComponent.WS>(component)
}

fun assertIsEOL(component: TextComponent) {
    assertIs<TextComponent.EOL>(component)
}

fun assertIsBR(component: TextComponent) {
    assertIs<TextComponent.BR>(component)
}

fun assertIsSQUOTE(component: TextComponent) {
    assertIs<TextComponent.SQUOTE>(component)
}

fun assertIsMono(component: TextComponent, character: Char) {
    assertIs<TextComponent.Mono>(component)
    assertEquals(component.text, character.toString())
}

fun assertIsCodeSpan(component: TextComponent, text: String) {
    assertIs<TextComponent.CodeSpan>(component)
    assertEquals(text, component.text)
}

fun assertIsLink(component: TextComponent, text: String, url: String = text) {
    assertIs<TextComponent.Link>(component)
    assertEquals(text, component.text)
    assertEquals(url, component.url)
}

fun assertIsStyledText(
    component: TextComponent,
    text: String,
    isBold: Boolean = false,
    isItalic: Boolean = false,
    isStrikethrough: Boolean = false
) {
    assertIs<TextComponent.StyledText>(component)
    assertEquals(text, component.text)
    assertEquals(
        isBold,
        component.isBold,
        "Expected component to ${if (isBold) "" else "not "}be bold."
    )
    assertEquals(
        isItalic,
        component.isItalic,
        "Expected component to ${if (isItalic) "" else "not "}be italic."
    )
    assertEquals(
        isStrikethrough,
        component.isStrikethrough,
        "Expected component to ${if (isStrikethrough) "" else "not "}be strikethrough."
    )
}
