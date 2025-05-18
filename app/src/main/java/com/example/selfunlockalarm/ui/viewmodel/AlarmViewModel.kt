package com.example.selfunlockalarm.ui.viewmodel

import android.app.AlarmManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.selfunlockalarm.domain.usecase.AlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * アラーム画面のViewModel
 * UIの状態管理とユーザーアクションの処理を担当
 */
@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmManager: AlarmManager,
    private val alarmUseCase: AlarmUseCase
) : ViewModel() {

    // UI状態
    private val _uiState = MutableStateFlow(AlarmUiState())
    val uiState: StateFlow<AlarmUiState> = _uiState.asStateFlow()

    init {
        // アラーム設定の変更を監視
        viewModelScope.launch {
            alarmUseCase.alarmSettings.collect { settings ->
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
     * アラーム時間を更新
     */
    fun updateAlarmTime(hour: Int, minute: Int) {
        alarmUseCase.updateAlarmTime(hour, minute)
    }

    /**
     * アラームの有効/無効を切り替え
     */
    fun toggleAlarm(enabled: Boolean) {
        alarmUseCase.toggleAlarm(enabled)
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

    /**
     * 正確なアラーム権限の状態を更新
     */
    fun updateExactAlarmPermissionState(canSchedule: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                canScheduleExactAlarms = canSchedule
            )
        }
    }
}

/**
 * アラーム画面のUI状態
 */
data class AlarmUiState(
    val isAlarmEnabled: Boolean = false,
    val selectedHour: Int = 7,
    val selectedMinute: Int = 0,
    val hasNotificationPermission: Boolean = false,
    val canScheduleExactAlarms: Boolean = true
)
