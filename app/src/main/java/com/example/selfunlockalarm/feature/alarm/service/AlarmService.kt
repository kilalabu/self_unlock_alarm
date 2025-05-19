package com.example.selfunlockalarm.feature.alarm.service

import android.app.*
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import androidx.core.app.NotificationCompat
import com.example.selfunlockalarm.R
import com.example.selfunlockalarm.feature.unlock.UnlockActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmSoundService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    companion object {
        const val ACTION_START_ALARM = "com.example.selfunlockalarm.ACTION_START_ALARM_SERVICE"
        const val ACTION_STOP_ALARM = "com.example.selfunlockalarm.ACTION_STOP_ALARM_SERVICE"
        const val CHANNEL_ID = "alarm_sound_channel" // AlarmReceiverと別のIDが良い場合もある
        const val NOTIFICATION_ID = 1002 // AlarmReceiverの通知IDと区別
    }

    override fun onCreate() {
        super.onCreate()
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VibratorManager::class.java)
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Vibrator::class.java)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_ALARM -> {
                startForegroundService()
                startAlarmSoundAndVibration()
            }
            ACTION_STOP_ALARM -> {
                stopAlarmSoundAndVibration()
                stopSelf()
            }
        }
        return START_STICKY // 異常終了しても再起動を試みる
    }

    private fun startForegroundService() {
        createNotificationChannel()

        val pinEntryIntent = Intent(this, UnlockActivity::class.java).apply {
            // 必要に応じてフラグを設定 (例: 新しいタスクで開くなど)
            // flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            pinEntryIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("起床時間です！")
            .setContentText("タップしてアラームを解除してください。")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 適切なアイコンに
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent) // タップでPinEntryActivityを開く
            .setOngoing(true) // ユーザーがスワイプで消せないようにする
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun startAlarmSoundAndVibration() {
        // アラーム音の再生
        try {
            val alarmSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // フォールバック

            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@AlarmSoundService, alarmSoundUri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                prepareAsync() // 非同期で準備
                setOnPreparedListener { start() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 1000), 0)) // 0.5秒振動、1秒停止を繰り返す
    }

    private fun stopAlarmSoundAndVibration() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        vibrator?.cancel()
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "アラーム実行中通知",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "アラームが鳴動中です"
            setSound(null, null) // サービスが音を管理するので、通知チャンネルの音はなし
            enableVibration(false) // サービスがバイブを管理
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarmSoundAndVibration() // サービス破棄時にも確実に停止
    }
}