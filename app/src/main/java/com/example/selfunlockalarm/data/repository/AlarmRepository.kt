package com.example.selfunlockalarm.data.repository

import android.content.SharedPreferences
import com.example.selfunlockalarm.receiver.AlarmReceiver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * アラーム設定のリポジトリ
 * アラーム設定の保存と取得を担当
 */
class AlarmRepository @Inject constructor(
    private val sharedPrefs: SharedPreferences
) {

    // アラーム設定の状態
    private val _alarmSettings = MutableStateFlow(
        AlarmSettings(
            isEnabled = false,
            hour = 7,
            minute = 0
        )
    )
    val alarmSettings: Flow<AlarmSettings> = _alarmSettings.asStateFlow()

    init {
        // 初期化時にSharedPreferencesから設定を読み込む
        loadAlarmSettings()
    }

    /**
     * SharedPreferencesからアラーム設定を読み込む
     */
    private fun loadAlarmSettings() {
        val isEnabled = sharedPrefs.getBoolean(AlarmReceiver.PREF_ALARM_ENABLED, false)
        val hour = sharedPrefs.getInt(AlarmReceiver.PREF_HOUR, 7)
        val minute = sharedPrefs.getInt(AlarmReceiver.PREF_MINUTE, 0)

        _alarmSettings.update {
            AlarmSettings(
                isEnabled = isEnabled,
                hour = hour,
                minute = minute
            )
        }
    }

    /**
     * アラーム設定を保存する
     */
    fun saveAlarmSettings(settings: AlarmSettings) {
        sharedPrefs.edit().apply {
            putBoolean(AlarmReceiver.PREF_ALARM_ENABLED, settings.isEnabled)
            putInt(AlarmReceiver.PREF_HOUR, settings.hour)
            putInt(AlarmReceiver.PREF_MINUTE, settings.minute)
            apply()
        }

        // 状態を更新
        _alarmSettings.update { settings }
    }

    /**
     * アラームの有効/無効を切り替える
     */
    fun setAlarmEnabled(enabled: Boolean) {
        val currentSettings = _alarmSettings.value
        saveAlarmSettings(currentSettings.copy(isEnabled = enabled))
    }

    /**
     * アラーム時間を更新する
     */
    fun updateAlarmTime(hour: Int, minute: Int) {
        val currentSettings = _alarmSettings.value
        saveAlarmSettings(currentSettings.copy(hour = hour, minute = minute))
    }
}

/**
 * アラーム設定データクラス
 */
data class AlarmSettings(
    val isEnabled: Boolean,
    val hour: Int,
    val minute: Int
)
