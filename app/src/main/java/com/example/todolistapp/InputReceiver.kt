package com.example.todolistapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.app.RemoteInput

class InputReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 1. Получаем введенный текст
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        val taskText = remoteInput?.getCharSequence("key_input")?.toString()

        if (!taskText.isNullOrEmpty()) {
            // 2. Сохраняем в SharedPreferences
            val prefs = getSharedPrefs(context)
            saveTask(prefs, taskText)

            // 3. Можно показать уведомление о успешном добавлении
            NotificationHelper.showSuccessNotification(context, "Задача добавлена!")
        }
    }

    private fun getSharedPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    }

    private fun saveTask(prefs: SharedPreferences, text: String) {
        // Получаем текущий список задач
        val existingTasks = prefs.getStringSet("tasks", mutableSetOf())?.toMutableSet()
            ?: mutableSetOf()

        // Добавляем новую задачу
        existingTasks.add(text)

        // Сохраняем обновленный список
        prefs.edit().apply {
            putStringSet("tasks", existingTasks)
            apply()
        }
    }

    companion object {
        // Для удобства получения задач из Activity
        fun getTasks(context: Context): Set<String> {
            return context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .getStringSet("tasks", emptySet()) ?: emptySet()
        }
    }
}