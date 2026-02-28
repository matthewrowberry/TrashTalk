package com.usuhackathon.trashtalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.usuhackathon.trashtalk.ui.AppNav
import com.usuhackathon.trashtalk.ui.theme.TrashTalkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrashTalkTheme {
                AppNav()
            }
        }
    }
}