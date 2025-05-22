package com.example.selfunlockalarm.feature.alarm.setting.viewmodel

import android.app.AlarmManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selfunlockalarm.feature.alarm.AlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmSettingViewModel @Inject constructor(
    private val alarmManager: AlarmManager,
    private val alarmUseCase: AlarmUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlarmSettingUiState())
    val uiState: StateFlow<AlarmSettingUiState> = _uiState.asStateFlow()

    init {
        // アラーム設定の変更を監視
        viewModelScope.launch {
            alarmUseCase.alarmSetting.collect { settings ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isAlarmEnabled = settings.isEnabled,
                        selectedHour = settings.hour,
                        selectedMinute = settings.minute
                    )
                }
            }
        }

        // Android 12以降の場合、正確なアラーム権限をチェック
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            _uiState.update { currentState ->
                currentState.copy(
                    canScheduleExactAlarms = alarmManager.canScheduleExactAlarms()
                )
            }
        }
    }

    /**
     * 時間設定を表示
     */
    fun onTimeClick() {
        _uiState.update { currentState ->
            currentState.copy(
                timePickerState = AlarmSettingUiState.TimePickerState.Shown(
                    hour = currentState.selectedHour,
                    minute = currentState.selectedMinute
                )
            )
        }
    }

    /**
     * アラーム時間を更新
     */
    fun updateAlarmTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            alarmUseCase.updateAlarmTime(
                hour = hour,
                minute = minute,
                isEnabled = uiState.value.isAlarmEnabled
            )
        }
        _uiState.update { currentState ->
            currentState.copy(
                timePickerState = AlarmSettingUiState.TimePickerState.Dismissed,
            )
        }
    }

    /**
     * 時間設定を閉じる
     */
    fun onTimePickerDismiss() {
        _uiState.update { currentState ->
            currentState.copy(
                timePickerState = AlarmSettingUiState.TimePickerState.Dismissed
            )
        }
    }

    /**
     * アラームの有効/無効を切り替え
     */
    fun toggleAlarm(enabled: Boolean) {
        viewModelScope.launch {
            alarmUseCase.toggleAlarm(
                enabled = enabled,
                hour = uiState.value.selectedHour,
                minute = uiState.value.selectedMinute
            )
        }
    }

    /**
     * 通知権限の状態を更新
     */
    fun updateNotificationPermissionState(granted: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                hasNotificationPermission = granted
            )
        }
    }
}

/**
 * アラーム設定画面のUI状態
 */
data class AlarmSettingUiState(
    val isAlarmEnabled: Boolean = false,
    val selectedHour: Int = 7,
    val selectedMinute: Int = 0,
    val hasNotificationPermission: Boolean = false,
    val canScheduleExactAlarms: Boolean = true,
    val timePickerState: TimePickerState = TimePickerState.Dismissed,
) {
    sealed interface TimePickerState {
        data class Shown(val hour: Int, val minute: Int) : TimePickerState
        object Dismissed : TimePickerState
    }
}
