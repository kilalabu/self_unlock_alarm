package com.example.selfunlockalarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.example.selfunlockalarm.app.theme.SelfUnlockAlarmTheme
import com.example.selfunlockalarm.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SelfUnlockAlarmTheme {
                AppNavHost(
                    modifier = Modifier.Companion.padding()
                )
            }
        }
    }
}