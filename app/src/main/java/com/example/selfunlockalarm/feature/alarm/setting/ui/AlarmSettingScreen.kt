package com.example.selfunlockalarm.feature.alarm.setting.ui

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.selfunlockalarm.feature.alarm.setting.viewmodel.AlarmSettingViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmSettingScreen(
    modifier: Modifier = Modifier,
    viewModel: AlarmSettingViewModel = hiltViewModel(),
    onNavigateExactAlarmPermissionSettings: () -> Unit,
    onNavigateToPinSetting: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showMenu by remember { mutableStateOf(false) }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "メニュー"
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("PINコード設定") },
                            onClick = {
                                showMenu = false
                                onNavigateToPinSetting()
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
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
            modifier = modifier.padding(innerPadding)
        )
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
