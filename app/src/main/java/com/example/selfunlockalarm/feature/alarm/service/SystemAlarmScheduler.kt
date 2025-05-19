package com.example.selfunlockalarm.feature.alarm.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.example.selfunlockalarm.feature.alarm.receiver.AlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject

class SystemAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager
) {

    /**
     * 毎日特定の時間にアラームをスケジュールする
     * @param hourOfDay 時間（24時間形式）
     * @param minute 分
     */
    fun scheduleAlarm(hourOfDay: Int, minute: Int) {
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            Intent.setAction = AlarmReceiver.ACTION_ALARM
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 次のアラーム時刻を設定
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // 設定時刻が現在時刻より前の場合は翌日に設定
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        // Android 12以降はアラームの正確なタイミングに制限があるため、
        // アプリがフォアグラウンドにある場合のみ正確なアラームを使用できる
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, // 指定時刻になるとデバイスをスリープ解除して処理を実行
                    calendar.timeInMillis,
                    pendingIntent
                )
                showAlarmSetToast(hourOfDay, minute, calendar.timeInMillis)
            } else {
                // 正確なアラームの権限がない場合は、非正確なアラームを設定
                setInexactRepeatingAlarm(calendar.timeInMillis, pendingIntent)
                Toast.makeText(
                    context,
                    "正確なアラームを設定するには設定から権限を付与してください",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            // Android 12未満の場合
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            showAlarmSetToast(hourOfDay, minute, calendar.timeInMillis)
        }
    }

    /**
     * 非正確な繰り返しアラームを設定
     */
    private fun setInexactRepeatingAlarm(triggerAtMillis: Long, pendingIntent: PendingIntent) {
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    /**
     * アラームをキャンセルする
     */
    fun cancelAlarm() {
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            Intent.setAction = AlarmReceiver.ACTION_ALARM
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        Toast.makeText(context, "アラームをキャンセルしました", Toast.LENGTH_SHORT).show()
    }

    /**
     * アラーム設定完了のトーストを表示
     */
    private fun showAlarmSetToast(hourOfDay: Int, minute: Int, timeInMillis: Long) {
        val timeString = String.format("%02d:%02d", hourOfDay, minute)
        Toast.makeText(
            context,
            "アラームを $timeString に設定しました",
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        // PendingIntentを識別するための一意なID
        private const val ALARM_REQUEST_CODE = 100
    }
}