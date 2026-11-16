package com.nexo.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nexo.R
import com.nexo.security.TokenManager

class StreakReminderWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val tokenManager = TokenManager(appContext)

        // Só notifica se o usuário estiver logado e ainda NÃO tiver estudado hoje
        if (tokenManager.getAccessToken().isNullOrBlank() || tokenManager.hasStudiedToday()) {
            return Result.success()
        }

        showStreakNotification()
        return Result.success()
    }

    private fun showStreakNotification() {
        val channelId = "nexo_streak_channel"
        val manager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Lembretes de Ofensiva",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.mascote_default)
            .setContentTitle("Não deixe seu foguinho apagar! 🔥")
            .setContentText("Conclua um tópico rápido hoje no Nexo Academy para manter sua ofensiva.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(appContext).notify(1001, notification)
    }
}