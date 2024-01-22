package com.example.cs308_mobileapplication

import android.content.Context
import android.net.Uri
import com.example.cs308_mobileapplication.data.addedSongs
import com.example.cs308_mobileapplication.pages.Addsongs
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.io.ByteArrayInputStream
import java.io.InputStream

class AddsongsTest {

    private lateinit var addsongs: Addsongs
    private lateinit var mockContext: Context
    private lateinit var mockUri: Uri
    private lateinit var inputStream: InputStream

    @Before
    fun setUp() {
        addsongs = Addsongs()
        mockContext = mock(Context::class.java)
        mockUri = mock(Uri::class.java)

        // Example CSV content
        val csvContent = "Song1,Album1,5,Artist1,Genre1\nSong2,Album2,,Artist2,Genre2"
        inputStream = ByteArrayInputStream(csvContent.toByteArray(Charsets.UTF_8))

        `when`(mockContext.contentResolver.openInputStream(mockUri)).thenReturn(inputStream)
    }

    @Test
    fun readCsvFile_correctlyParsesContent() {
        val expected = mutableListOf(
            addedSongs("Song1", "Album1", 5, "Artist1", "Genre1"),
            addedSongs("Song2", "Album2", null, "Artist2", "Genre2")
        )

        val result = addsongs.readCsvFile(mockContext, mockUri)

        assertEquals(expected, result)
    }
}
