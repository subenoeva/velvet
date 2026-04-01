package com.subenoeva.velvet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.subenoeva.velvet.core.ui.theme.VelvetTheme
import com.subenoeva.velvet.navigation.VelvetNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VelvetTheme {
                VelvetNavGraph()
            }
        }
    }
}
