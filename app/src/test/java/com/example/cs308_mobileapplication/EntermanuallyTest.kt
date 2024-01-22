import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import com.example.cs308_mobileapplication.pages.Entermanually

import org.junit.Test

class EntermanuallyTest {
    private val entermanually = Entermanually()

    @Test
    fun `validateSongInput returns false when song name is empty`() {
        val result = entermanually.validateSongInput("", "Artist", "Album", "Genre")
        assertFalse(result)
    }

    @Test
    fun `validateSongInput returns false when artist name is empty`() {
        val result = entermanually.validateSongInput("Song", "", "Album", "Genre")
        assertFalse(result)
    }

    @Test
    fun `validateSongInput returns false when album name is empty`() {
        val result = entermanually.validateSongInput("Song", "Artist", "", "Genre")
        assertFalse(result)
    }

    @Test
    fun `validateSongInput returns false when genre is Select Genre`() {
        val result = entermanually.validateSongInput("Song", "Artist", "Album", "Select Genre")
        assertFalse(result)
    }

    @Test
    fun `validateSongInput returns true for valid input`() {
        val result = entermanually.validateSongInput("Song", "Artist", "Album", "Genre")
        assertTrue(result)
    }
}
