package com.nexo.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object StreakNotificationScheduler {

    private data class ReminderSlot(
        val uniqueName: String,
        val time: LocalTime,
        val urgencyLevel: Int
    )

    private val slots = listOf(
        ReminderSlot("nexo_streak_8h", LocalTime.of(8,0), urgencyLevel = 1),
        ReminderSlot("nexo_streak_14h", LocalTime.of(14, 0), urgencyLevel = 1),
        ReminderSlot("nexo_streak_19h", LocalTime.of(19, 0), urgencyLevel = 2),
        ReminderSlot("nexo_streak_22h", LocalTime.of(22, 0), urgencyLevel = 3),
        ReminderSlot("nexo_streak_23h30", LocalTime.of(23, 30), urgencyLevel = 4)
    )

    fun scheduleAll(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val now = LocalDateTime.now()

        slots.forEach { slot ->
            var nextRun = now.with(slot.time)
            if (!nextRun.isAfter(now)) {
                nextRun = nextRun.plusDays(1)
            }

            val initialDelayMinutes = Duration.between(now, nextRun).toMinutes()

            val request = PeriodicWorkRequestBuilder<StreakReminderWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
                .setInputData(workDataOf(StreakReminderWorker.KEY_URGENCY_LEVEL to slot.urgencyLevel))
                .build()

            workManager.enqueueUniquePeriodicWork(
                slot.uniqueName,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}

