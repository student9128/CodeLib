package com.kevin.codelib.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.kevin.codelib.theme.AlbumTheme

abstract class BaseComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlbumTheme {
                ComposeActivityContent()
            }
        }
    }

    @Composable
    abstract fun ComposeActivityContent()

}