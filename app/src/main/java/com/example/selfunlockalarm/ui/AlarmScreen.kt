package com.example.selfunlockalarm.ui

import android.Manifest
import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.selfunlockalarm.alarm.AlarmManagerHelper
import com.example.selfunlockalarm.receiver.AlarmReceiver
import java.util.Calendar

@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // アラーム設定の状態を管理
    var alarmEnabled by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableStateOf(7) } // デフォルト7時
    var selectedMinute by remember { mutableStateOf(0) } // デフォルト0分

    // SharedPreferencesからアラーム設定を読み込む
    LaunchedEffect(key1 = Unit) {
        val sharedPrefs = context.getSharedPreferences(
            AlarmReceiver.ALARM_PREFS,
            Context.MODE_PRIVATE
        )

        alarmEnabled = sharedPrefs.getBoolean(AlarmReceiver.PREF_ALARM_ENABLED, false)
        selectedHour = sharedPrefs.getInt(AlarmReceiver.PREF_HOUR, 7)
        selectedMinute = sharedPrefs.getInt(AlarmReceiver.PREF_MINUTE, 0)
    }

    // 通知権限のリクエスト
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // 権限が付与された場合の処理
            if (alarmEnabled) {
                scheduleAlarm(context, selectedHour, selectedMinute)
            }
        }
    }

    // 画面表示時に通知権限をチェック
    LaunchedEffect(key1 = Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionState = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )

            if (permissionState != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "起床アラーム設定",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 現在設定されている時間の表示
                Text(
                    text = String.format("%02d:%02d", selectedHour, selectedMinute),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 時間選択ボタン
                Button(
                    onClick = {
                        showTimePickerDialog(context) { hour, minute ->
                            selectedHour = hour
                            selectedMinute = minute

                            // 選択した時間をSharedPreferencesに保存
                            saveAlarmSettings(context, alarmEnabled, hour, minute)

                            // アラームが有効な場合は再設定
                            if (alarmEnabled) {
                                scheduleAlarm(context, hour, minute)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("時間を選択")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // アラーム有効/無効スイッチ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "アラームを有効にする",
                        fontSize = 18.sp
                    )

                    Switch(
                        checked = alarmEnabled,
                        onCheckedChange = { isEnabled ->
                            alarmEnabled = isEnabled

                            // アラーム設定の保存
                            saveAlarmSettings(context, isEnabled, selectedHour, selectedMinute)

                            if (isEnabled) {
                                // アラームを設定
                                scheduleAlarm(context, selectedHour, selectedMinute)
                            } else {
                                // アラームをキャンセル
                                val alarmHelper = AlarmManagerHelper(context)
                                alarmHelper.cancelAlarm()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Android 12以降で正確なアラーム権限が必要な場合の設定ボタン
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    if (!alarmManager.canScheduleExactAlarms()) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(
                                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                    Uri.parse("package:${context.packageName}")
                                )
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("正確なアラーム権限を設定")
                        }
                    }
                }
            }
        }
    }
}

/**
 * 時間選択ダイアログを表示
 */
private fun showTimePickerDialog(
    context: Context,
    onTimeSelected: (Int, Int) -> Unit
) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            onTimeSelected(selectedHour, selectedMinute)
        },
        hour,
        minute,
        true // 24時間表示
    ).show()
}

/**
 * アラーム設定をSharedPreferencesに保存
 */
private fun saveAlarmSettings(
    context: Context,
    enabled: Boolean,
    hour: Int,
    minute: Int
) {
    val sharedPrefs = context.getSharedPreferences(
        AlarmReceiver.ALARM_PREFS,
        Context.MODE_PRIVATE
    )

    sharedPrefs.edit().apply {
        putBoolean(AlarmReceiver.PREF_ALARM_ENABLED, enabled)
        putInt(AlarmReceiver.PREF_HOUR, hour)
        putInt(AlarmReceiver.PREF_MINUTE, minute)
        apply()
    }
}

/**
 * アラームをスケジュール
 */
private fun scheduleAlarm(
    context: Context,
    hour: Int,
    minute: Int
) {
    val alarmHelper = AlarmManagerHelper(context)
    alarmHelper.scheduleAlarm(hour, minute)
}
