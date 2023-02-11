package com.arnyminerz.markdowntext.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.arnyminerz.markdowntext.image.TableRenderer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                // MarkdownTextPreview(MarkdownFlavour.Github)

                Image(
                    bitmap = TableRenderer(
                        listOf("Column 1", "Column 2", "Column 3"),
                        listOf(
                            listOf("Value 1", "Value 2", "Value 3"),
                            listOf("Value 4", "Value 5", "Value 6"),
                            listOf("Value 7", "Value 8", "Value 9"),
                        )
                    ).imageBitmap,
                    contentDescription = "",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
