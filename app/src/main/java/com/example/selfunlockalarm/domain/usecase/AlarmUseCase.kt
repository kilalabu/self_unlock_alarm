package com.example.selfunlockalarm.domain.usecase

import com.example.selfunlockalarm.alarm.AlarmManagerHelper
import com.example.selfunlockalarm.data.repository.AlarmRepository
import com.example.selfunlockalarm.domain.model.AlarmSetting
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmUseCase @Inject constructor(
    private val alarmHelper: AlarmManagerHelper,
    private val alarmRepository: AlarmRepository
) {
    /**
     * アラーム設定の変更を監視
     */
    val alarmSetting: Flow<AlarmSetting> = alarmRepository.alarmSetting

    /**
     * アラームの有効/無効を切り替える
     */
    suspend fun toggleAlarm(enabled: Boolean, hour: Int, minute: Int) {
        alarmRepository.setAlarmEnabled(enabled)
        if (enabled) {
            // アラームを有効化
            alarmHelper.scheduleAlarm(hour, minute)
        } else {
            // アラームをキャンセル
            alarmHelper.cancelAlarm()
        }
    }

    /**
     * アラーム時間を更新する
     * アラームが有効な場合は再スケジュールする
     */
    suspend fun updateAlarmTime(hour: Int, minute: Int, isEnabled: Boolean) {
        alarmRepository.updateAlarmTime(hour, minute)
        
        // アラームが有効な場合は再スケジュール
        if (isEnabled) {
            alarmHelper.scheduleAlarm(hour, minute)
        }
    }
}
