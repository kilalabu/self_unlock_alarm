package com.example.selfunlockalarm.feature.alarm.setting.ui

import android.Manifest
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.selfunlockalarm.uicommon.theme.SelfUnlockAlarmTheme
import com.example.selfunlockalarm.uicommon.theme.TextBlue
import com.example.selfunlockalarm.feature.alarm.setting.viewmodel.AlarmSettingUiState
import com.example.selfunlockalarm.feature.alarm.setting.viewmodel.AlarmSettingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmSettingScreen(
    viewModel: AlarmSettingViewModel = hiltViewModel(),
    onNavigateExactAlarmPermissionSettings: () -> Unit,
    onNavigateToPinSetting: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    AlarmSettingScreen(
        uiState = uiState,
        onNavigateExactAlarmPermissionSettings = onNavigateExactAlarmPermissionSettings,
        onNavigateToPinSetting = onNavigateToPinSetting,
        onUpdateNotificationPermissionState = { isGranted ->
            viewModel.updateNotificationPermissionState(isGranted)
        },
        onTimeClick = {
            viewModel.onTimeClick()
        },
        onUpdateAlarmTime = { hour, minute ->
            viewModel.updateAlarmTime(hour, minute)
        },
        onToggleAlarm = { enabled ->
            viewModel.toggleAlarm(enabled)
        },
        onTimePickerDismiss = {
            viewModel.onTimePickerDismiss()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlarmSettingScreen(
    uiState: AlarmSettingUiState,
    onNavigateExactAlarmPermissionSettings: () -> Unit,
    onNavigateToPinSetting: () -> Unit,
    onUpdateNotificationPermissionState: (Boolean) -> Unit,
    onTimeClick: () -> Unit,
    onUpdateAlarmTime: (hour: Int, minute: Int) -> Unit,
    onToggleAlarm: (Boolean) -> Unit,
    onTimePickerDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onUpdateNotificationPermissionState(isGranted)
    }

    LaunchedEffect(key1 = Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionState = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            onUpdateNotificationPermissionState(permissionState == PackageManager.PERMISSION_GRANTED)
            if (permissionState != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            onUpdateNotificationPermissionState(true)
        }
    }

    SelfUnlockAlarmTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    actions = {
                        IconButton(
                            onClick = { showMenu = !showMenu },
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "メニュー",
                                tint = TextBlue
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text("PINコード設定")
                                },
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
                onTimeClick = onTimeClick,
                onToggleAlarm = { enabled ->
                    onToggleAlarm(enabled)
                },
                onRequestExactAlarmPermission = {
                    onNavigateExactAlarmPermissionSettings()
                },
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            )

            when (val state = uiState.timePickerState) {
                is AlarmSettingUiState.TimePickerState.Shown -> {
                    TimePickerDialog(
                        hour = state.hour,
                        minute = state.minute,
                        onConfirm = { timePickerState ->
                            onUpdateAlarmTime(
                                timePickerState.hour,
                                timePickerState.minute
                            )
                        },
                        onDismiss = onTimePickerDismiss
                    )
                }

                AlarmSettingUiState.TimePickerState.Dismissed -> {}
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun AlarmSettingScreenPreview() {
    AlarmSettingScreen(
        uiState = AlarmSettingUiState(
            selectedHour = 7,
            selectedMinute = 30,
            isAlarmEnabled = true,
            canScheduleExactAlarms = false,
        ),
        onNavigateExactAlarmPermissionSettings = {},
        onNavigateToPinSetting = {},
        onUpdateNotificationPermissionState = {},
        onTimeClick = {},
        onUpdateAlarmTime = { _, _ -> },
        onToggleAlarm = {},
        onTimePickerDismiss = {}
    )
}