package com.example.selfunlockalarm.data.repository

import com.example.selfunlockalarm.data.datasource.AlarmPreferencesDataSource
import com.example.selfunlockalarm.domain.model.AlarmSetting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(
    private val alarmPreferencesDataSource: AlarmPreferencesDataSource
) {
    /**
     * アラーム設定の変更を監視
     */
    val alarmSetting: Flow<AlarmSetting> = alarmPreferencesDataSource.alarmPreferencesFlow
        .map { preferences ->
            AlarmSetting(
                isEnabled = preferences.isEnabled,
                hour = preferences.hour,
                minute = preferences.minute
            )
        }

    /**
     * アラームの有効/無効を切り替える
     */
    suspend fun setAlarmEnabled(enabled: Boolean) {
        alarmPreferencesDataSource.updateAlarmPreferences(isEnabled = enabled)
    }

    /**
     * アラーム時間を更新する
     */
    suspend fun updateAlarmTime(hour: Int, minute: Int) {
        alarmPreferencesDataSource.updateAlarmPreferences(hour = hour, minute = minute)
    }
}