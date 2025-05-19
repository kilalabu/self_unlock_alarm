package com.example.selfunlockalarm.alarm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.example.selfunlockalarm.alarm.setting.ui.AlarmSettingScreen
import com.example.selfunlockalarm.app.theme.SelfUnlockAlarmTheme
import com.example.selfunlockalarm.pin.PinSettingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SelfUnlockAlarmTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AlarmSettingScreen(
                        modifier = Modifier.padding(innerPadding),
                        onNavigateExactAlarmPermissionSettings = {
                            // 正確なアラーム権限を設定するための設定画面を開く
                            val intent =
                                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                    data = "package:${packageName}".toUri()
                                }
                            startActivity(intent)
                        },
                        onNavigateToPinSetting = {
                            // PINコード設定画面に遷移
                            val intent = Intent(this, PinSettingActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}
