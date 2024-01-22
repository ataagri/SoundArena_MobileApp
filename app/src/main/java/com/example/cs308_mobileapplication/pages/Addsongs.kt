package com.example.cs308_mobileapplication.pages

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cs308_mobileapplication.R
import com.example.cs308_mobileapplication.data.addedSongs
import java.io.BufferedReader
import java.io.InputStreamReader

class Addsongs : ComponentActivity() {

    companion object {
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1
        private const val REQUEST_CODE_CSV = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addsongs)
        val uploadButton = findViewById<Button>(R.id.lmao)
        val enterManuallyButton = findViewById<Button>(R.id.manualButton)
        val mainMenuButton = findViewById<Button>(R.id.mainMenuButton)

        mainMenuButton.setOnClickListener {
            val toMainMenu = Intent(this, Mainpage::class.java)
            startActivity(toMainMenu)
        }

        enterManuallyButton.setOnClickListener {
            val toEnterManually = Intent(this, Entermanually::class.java)
            startActivity(toEnterManually)
        }
        uploadButton.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                )
            } else {
                openFileChooser()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CSV && resultCode == RESULT_OK) {
            val selectedFileUri: Uri? = data?.data
            selectedFileUri?.let {
                val songs = readCsvFile(this, it)
                displaySongs(songs)
            }
        }
    }

    private fun displaySongs(songs: List<addedSongs>) {
        val container: LinearLayout = findViewById(R.id.allSongsContainer) // Adjust ID as necessary
        songs.forEach { song ->
            val songView = LayoutInflater.from(this).inflate(R.layout.song_view, container, false)

            // Assuming song_view.xml has TextViews for title, album, rating, performer, and genre
            songView.findViewById<TextView>(R.id.name).text = song.title
            songView.findViewById<TextView>(R.id.album).text = song.album
            songView.findViewById<TextView>(R.id.rating).text = song.rating?.toString() ?: "Not Rated"
            songView.findViewById<TextView>(R.id.artists).text = song.performer
            songView.findViewById<TextView>(R.id.genre).text = song.genre

            container.addView(songView)
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a CSV file"), REQUEST_CODE_CSV)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, "Please install a file manager", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission was granted, open file chooser
                    openFileChooser()
                } else {
                    // Permission denied, handle the feature's limitation
                    Toast.makeText(
                        this,
                        "Permission denied to read your External storage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    fun readCsvFile(context: Context, fileUri: Uri): MutableList<addedSongs> {
        val csvSongs = mutableListOf<addedSongs>()

        context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val tokens = line!!.split(",")
                if (tokens.size >= 5) {
                    val title = tokens[0]
                    val album = tokens[1]
                    val rating = if (tokens[2].isNotEmpty()) tokens[2].toIntOrNull() else null
                    val performer = tokens[3]
                    val genre = tokens[4]

                    csvSongs.add(addedSongs(title, album, rating, performer, genre))
                }
            }
        }
        return csvSongs
    }
}