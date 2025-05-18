package com.example.selfunlockalarm.domain.usecase

import com.example.selfunlockalarm.alarm.AlarmManagerHelper
import com.example.selfunlockalarm.data.repository.AlarmRepository
import com.example.selfunlockalarm.data.repository.AlarmSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * アラーム機能のユースケース
 * アラームの設定、キャンセル、状態取得などのビジネスロジックを担当
 */
class AlarmUseCase @Inject constructor(
    private val alarmHelper: AlarmManagerHelper,
    private val alarmRepository: AlarmRepository
) {
    // アラーム設定の状態を取得
    val alarmSettings: Flow<AlarmSettings> = alarmRepository.alarmSettings

    /**
     * アラームを設定する
     */
    fun scheduleAlarm(hour: Int, minute: Int) {
        // リポジトリに時間を保存
        alarmRepository.updateAlarmTime(hour, minute)

        // アラームを有効化
        alarmRepository.setAlarmEnabled(true)

        // AlarmManagerHelperを使用してアラームをスケジュール
        alarmHelper.scheduleAlarm(hour, minute)
    }

    /**
     * アラームをキャンセルする
     */
    fun cancelAlarm() {
        // アラームを無効化
        alarmRepository.setAlarmEnabled(false)

        // AlarmManagerHelperを使用してアラームをキャンセル
        alarmHelper.cancelAlarm()
    }

    /**
     * アラームの有効/無効を切り替える
     */
    fun toggleAlarm(enabled: Boolean) {
        // リポジトリの状態を更新
        alarmRepository.setAlarmEnabled(enabled)

//        if (enabled) {
//            // 現在の設定を取得
//            val currentSettings = alarmRepository.alarmSettings
//
//            // アラームを有効化
//            alarmHelper.scheduleAlarm(currentSettings.hour, currentSettings.minute)
//        } else {
//            // アラームをキャンセル
//            alarmHelper.cancelAlarm()
//        }
    }

    /**
     * アラーム時間を更新する
     * アラームが有効な場合は再スケジュールする
     */
    fun updateAlarmTime(hour: Int, minute: Int) {
        // 現在の設定を取得
//        val isEnabled = alarmRepository.alarmSettings.value.isEnabled
//
//        // リポジトリの状態を更新
//        alarmRepository.updateAlarmTime(hour, minute)
//
//        // アラームが有効な場合は再スケジュール
//        if (isEnabled) {
//            alarmHelper.scheduleAlarm(hour, minute)
//        }
    }
}
