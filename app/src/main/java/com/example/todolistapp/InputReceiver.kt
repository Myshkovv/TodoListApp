package com.example.todolistapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.RemoteInput

class InputReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        // Получаем введенный текст
        val inputText = RemoteInput.getResultsFromIntent(intent)
            ?.getCharSequence("key_input")
            ?.toString()

        if (!inputText.isNullOrEmpty()) {
            // Просто показываем Toast (для теста)
            Toast.makeText(context, "Добавлено: $inputText", Toast.LENGTH_SHORT).show()

            // Здесь можно сохранить в SharedPreferences или БД
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val existingTasks = prefs.getStringSet(KEY_TASKS, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
            existingTasks.add(inputText)
            prefs.edit().putStringSet(KEY_TASKS, existingTasks).apply()
        }
    }

    companion object {
        private const val KEY_TASKS = "saved_tasks"
        private const val PREFS_NAME = "tasks_prefs"
    }
}