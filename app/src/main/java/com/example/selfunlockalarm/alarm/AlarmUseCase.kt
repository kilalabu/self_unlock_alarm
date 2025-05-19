package com.example.selfunlockalarm.alarm

import com.example.selfunlockalarm.alarm.service.SystemAlarmScheduler
import com.example.selfunlockalarm.data.repository.AlarmRepository
import com.example.selfunlockalarm.data.repository.PinRepository
import com.example.selfunlockalarm.domain.model.AlarmSetting
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmUseCase @Inject constructor(
    private val alarmScheduler: SystemAlarmScheduler,
    private val alarmRepository: AlarmRepository,
    private val pinRepository: PinRepository
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
    
    /**
     * PINコードを取得する
     */
    suspend fun getPinCode(): String {
        return pinRepository.getPinCode()
    }
    
    /**
     * PINコードを更新する
     */
    suspend fun updatePinCode(pinCode: String) {
        pinRepository.updatePinCode(pinCode)
    }
}
