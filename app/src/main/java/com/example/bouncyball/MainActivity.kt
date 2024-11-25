package com.example.bouncyball

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gradients = listOf(
            Pair(Color(0xFFceb121), Color(0xFF9025C6)),
            Pair(Color(0xFF2CB721), Color(0xFFC12D74)),
            Pair(Color(0xFF343BC4), Color(0xFFDE6624)),
            Pair(Color(0xFF1EC0AD), Color(0xFFD74B31))
        )
        val randomGradient = gradients.random()
        enableEdgeToEdge()
        setContent {
            GradientBackground(randomGradient.first, randomGradient.second)
            BouncyBallGame()
        }
    }
}


