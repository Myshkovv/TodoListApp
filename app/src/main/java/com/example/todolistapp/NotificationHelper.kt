package com.example.todolistapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput

object NotificationHelper {

    fun showInputNotification(context: Context) {
        // 1. Создаем канал
        createChannel(context)

        // 2. Поле для ввода
        val remoteInput = RemoteInput.Builder("key_input")
            .setLabel("Введите задачу")
            .build()

        // 3. Intent для обработки ввода (ИСПРАВЛЕНО)
        val intent = Intent(context, InputReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE // Важно!
        )

        // 4. Собираем уведомление
        val notification = NotificationCompat.Builder(context, "tasks_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Новая задача")
            .setContentText("Нажмите для ввода")
            .addAction(
                NotificationCompat.Action.Builder(
                    android.R.drawable.ic_input_add,
                    "Добавить",
                    pendingIntent
                ).addRemoteInput(remoteInput)
                    .build()
            )
            .build()

        // 5. Показываем
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(1, notification)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "tasks_channel",
                "Задачи",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}
