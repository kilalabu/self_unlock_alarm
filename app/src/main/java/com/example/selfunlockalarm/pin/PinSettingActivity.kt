package com.example.selfunlockalarm.pin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.selfunlockalarm.app.theme.SelfUnlockAlarmTheme
import com.example.selfunlockalarm.pin.ui.PinSettingScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PinSettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SelfUnlockAlarmTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PinSettingScreen(
                        modifier = Modifier.padding(innerPadding),
                        onNavigateBack = { finish() }
                    )
                }
            }
        }
    }
}
