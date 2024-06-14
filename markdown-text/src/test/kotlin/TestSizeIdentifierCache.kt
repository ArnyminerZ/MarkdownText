import com.arnyminerz.markdowntext.network.SizeIdentifierCache
import kotlin.test.Test
import kotlin.test.assertEquals

class TestSizeIdentifierCache {
    @Test
    fun `test muteUrl`() {
        assertEquals(
            expected = "example_com_image_jpg",
            SizeIdentifierCache.muteUrl("https://example.com/image.jpg")
        )
    }
}
