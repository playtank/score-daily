package com.playtank.scoredaily

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.playtank.scoredaily.ui.ScoreScreen
import com.playtank.scoredaily.ui.theme.ScoreDailyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScoreDailyTheme {
                ScoreScreen()
            }
        }
    }
}
