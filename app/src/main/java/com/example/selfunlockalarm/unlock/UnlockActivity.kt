package com.example.selfunlockalarm.unlock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.selfunlockalarm.app.theme.SelfUnlockAlarmTheme
import com.example.selfunlockalarm.unlock.ui.UnlockScreen
import dagger.hilt.android.AndroidEntryPoint

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
                        onPinVerificationResult = { isCorrect ->
                            if (isCorrect) {
                                // アラームを解除する処理を実行
                            } else {
                                //
                            }
                        }
                    )
                }
            }
        }
    }
}