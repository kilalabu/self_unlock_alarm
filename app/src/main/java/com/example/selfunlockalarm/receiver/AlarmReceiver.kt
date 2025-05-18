package com.example.selfunlockalarm.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.selfunlockalarm.MainActivity
import com.example.selfunlockalarm.R
import com.example.selfunlockalarm.alarm.AlarmManagerHelper

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_ALARM) {
            // アラーム通知を表示
            showAlarmNotification(context)

            // 次の日のアラームを設定（毎日繰り返し）
            val sharedPrefs = context.getSharedPreferences(ALARM_PREFS, Context.MODE_PRIVATE)
            val hourOfDay = sharedPrefs.getInt(PREF_HOUR, -1)
            val minute = sharedPrefs.getInt(PREF_MINUTE, -1)

            if (hourOfDay != -1 && minute != -1) {
                val alarmHelper = AlarmManagerHelper(context)
                alarmHelper.scheduleAlarm(hourOfDay, minute)
            }
        }

        // デバイスが再起動されたときにアラームを再設定
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // TODO: ここでアラームを再設定するロジックを実装
        }
    }

    private fun showAlarmNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 通知チャンネルの作成
        val channel = NotificationChannel(
            CHANNEL_ID,
            "アラーム通知",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "起床アラーム通知"

            // アラーム音の設定
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            setSound(alarmSound, audioAttributes)
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)


        // メインアクティビティを開くためのPendingIntent
        val contentIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // アラームを止めるためのPendingIntent
        val stopIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_STOP_ALARM
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 通知の作成
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("起床時間です")
            .setContentText("おはようございます！起きる時間です。")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSound(alarmSound)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "停止", stopPendingIntent)
            .build()

        // 通知の表示
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val ACTION_ALARM = "com.example.selfunlockalarm.ACTION_ALARM"
        const val ACTION_STOP_ALARM = "com.example.selfunlockalarm.ACTION_STOP_ALARM"
        const val CHANNEL_ID = "alarm_channel"
        const val NOTIFICATION_ID = 1001

        // SharedPreferences用の定数
        const val ALARM_PREFS = "alarm_prefs"
        const val PREF_HOUR = "hour"
        const val PREF_MINUTE = "minute"
        const val PREF_ALARM_ENABLED = "alarm_enabled"
    }
}
