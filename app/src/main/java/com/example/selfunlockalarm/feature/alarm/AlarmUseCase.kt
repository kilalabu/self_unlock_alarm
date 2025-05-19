package com.example.selfunlockalarm.feature.alarm

import com.example.selfunlockalarm.data.repository.AlarmRepository
import com.example.selfunlockalarm.domain.model.AlarmSetting
import com.example.selfunlockalarm.feature.alarm.service.SystemAlarmScheduler
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmUseCase @Inject constructor(
    private val alarmScheduler: SystemAlarmScheduler,
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
            alarmScheduler.scheduleAlarm(hour, minute)
        } else {
            // アラームをキャンセル
            alarmScheduler.cancelAlarm()
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
            alarmScheduler.scheduleAlarm(hour, minute)
        }
    }
}
