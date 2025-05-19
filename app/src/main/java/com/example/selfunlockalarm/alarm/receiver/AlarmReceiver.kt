package com.example.selfunlockalarm.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.selfunlockalarm.alarm.AlarmUseCase
import com.example.selfunlockalarm.alarm.service.AlarmSoundService
import com.example.selfunlockalarm.alarm.service.SystemAlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmScheduler: SystemAlarmScheduler

    @Inject
    lateinit var alarmUseCase: AlarmUseCase

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_ALARM -> {
                val serviceIntent = Intent(context, AlarmSoundService::class.java).apply {
                    action = AlarmSoundService.ACTION_START_ALARM
                }
                context.startForegroundService(serviceIntent)
            }

            // デバイスが再起動されたときにアラームを再設定
            Intent.ACTION_BOOT_COMPLETED -> {
                rescheduleAlarmIfNeeded()
            }
        }
    }

    /**
     * アラーム設定を確認し、必要に応じてアラームを再スケジュールする
     */
    private fun rescheduleAlarmIfNeeded() {
        val setting = runBlocking {
            alarmUseCase.alarmSetting.first()
        }

        val hourOfDay = setting.hour
        val minute = setting.minute
        val isEnabled = setting.isEnabled

        if (isEnabled && hourOfDay != -1 && minute != -1) {
            alarmScheduler.scheduleAlarm(hourOfDay, minute)
        }
    }

    companion object {
        const val ACTION_ALARM = "com.example.selfunlockalarm.ACTION_ALARM"
    }
}
