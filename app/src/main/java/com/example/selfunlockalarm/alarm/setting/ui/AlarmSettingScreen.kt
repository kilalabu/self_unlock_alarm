package com.example.selfunlockalarm.alarm.setting.ui

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.selfunlockalarm.alarm.setting.viewmodel.AlarmSettingViewModel
import java.util.Calendar

@Composable
fun AlarmSettingScreen(
    modifier: Modifier = Modifier,
    viewModel: AlarmSettingViewModel = hiltViewModel(),
    onNavigateExactAlarmPermissionSettings: () -> Unit,
    onNavigateToPinSetting: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // 通知権限のリクエスト
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updateNotificationPermissionState(isGranted)
    }

    // 画面表示時に通知権限をチェック
    LaunchedEffect(key1 = Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionState = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )

            viewModel.updateNotificationPermissionState(
                permissionState == PackageManager.PERMISSION_GRANTED
            )

            if (permissionState != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Android 13未満は通知権限が必要ない
            viewModel.updateNotificationPermissionState(true)
        }
    }

    AlarmSettingContent(
        uiState = uiState,
        onTimeClick = {
            showTimePickerDialog(context) { h, m ->
                viewModel.updateAlarmTime(h, m)
            }
        },
        onToggleAlarm = { enabled ->
            viewModel.toggleAlarm(enabled)
        },
        onRequestExactAlarmPermission = {
            onNavigateExactAlarmPermissionSettings()
        },
        onPinSettingClick = {
            onNavigateToPinSetting()
        },
        modifier = modifier
    )
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
