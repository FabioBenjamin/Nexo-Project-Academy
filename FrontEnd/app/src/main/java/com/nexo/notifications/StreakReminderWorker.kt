package com.nexo.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nexo.MainActivity
import com.nexo.R
import com.nexo.security.TokenManager
import com.nexo.ui.theme.AppStrings
import java.time.DayOfWeek
import java.time.LocalDate

class StreakReminderWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_URGENCY_LEVEL = "urgency_level"
    }

    override suspend fun doWork(): Result {
        val tokenManager = TokenManager(appContext)

        if (tokenManager.getAccessToken().isNullOrBlank()) {
            return Result.success()
        }

        if (!tokenManager.areNotificationsEnabled()) {
            return Result.success()
        }

        val today = LocalDate.now()
        val isWeekend = today.dayOfWeek == DayOfWeek.SATURDAY || today.dayOfWeek == DayOfWeek.SUNDAY
        if (isWeekend && !tokenManager.isStudyWeekendsEnabled()) {
            return Result.success()
        }

        if (tokenManager.hasStudiedToday()) {
            return Result.success()
        }

        val urgencyLevel = inputData.getInt(KEY_URGENCY_LEVEL, 1)
        showStreakNotification(urgencyLevel)
        return Result.success()
    }

    private fun showStreakNotification(urgencyLevel: Int) {
        val isCritical = urgencyLevel >= 3
        val channelId = "nexo_streak_channel"
        val manager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            AppStrings.notificationChannelName,
            if (isCritical) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val openAppIntent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            urgencyLevel,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.mascote_default)
            .setContentTitle(AppStrings.notificationStreakTitle(urgencyLevel))
            .setContentText(AppStrings.notificationStreakBody(urgencyLevel))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(AppStrings.notificationStreakBody(urgencyLevel))
            )
            .setPriority(if (isCritical) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(appContext).notify(1001, notification)
    }
}