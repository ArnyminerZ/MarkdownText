import com.arnyminerz.markdowntext.network.RemoteImageSizeIdentifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlinx.coroutines.runBlocking

class TestRemoteImageSizeIdentifier {
    private val urlJPG = "https://dummyimage.com/600x400/000/fff.jpg"
    private val urlPNG = "https://dummyimage.com/600x400/000/fff.png"

    @Test
    fun `test getImageSize JPG`() = runBlocking<Unit> {
        val size = RemoteImageSizeIdentifier.getImageSize(urlJPG)
        assertNotNull(size)
        val (width, height) = size
        assertEquals(600, width)
        assertEquals(400, height)

        assertFailsWith(IllegalArgumentException::class) {
            RemoteImageSizeIdentifier.getImageSize("https://example.com")
        }
    }

    @Test
    fun `test getImageSize PNG`() = runBlocking<Unit> {
        val size = RemoteImageSizeIdentifier.getImageSize(urlPNG)
        assertNotNull(size)
        val (width, height) = size
        assertEquals(600, width)
        assertEquals(400, height)

        assertFailsWith(IllegalArgumentException::class) {
            RemoteImageSizeIdentifier.getImageSize("https://example.com")
        }
    }
}
