package com.example.selfunlockalarm.feature.unlock

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.selfunlockalarm.MainActivity
import com.example.selfunlockalarm.feature.alarm.service.AlarmSoundService
import com.example.selfunlockalarm.app.theme.SelfUnlockAlarmTheme
import com.example.selfunlockalarm.feature.unlock.ui.UnlockScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlin.jvm.java

@AndroidEntryPoint
class UnlockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SelfUnlockAlarmTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UnlockScreen(
                        modifier = Modifier.padding(innerPadding),
                        onUnlockSuccess = {
                            val intent = Intent(this, AlarmSoundService::class.java).apply {
                                Intent.setAction = AlarmSoundService.ACTION_STOP_ALARM
                            }
                            startService(intent)

                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
}