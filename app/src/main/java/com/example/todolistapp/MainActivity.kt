package com.example.todolistapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.main)

        createNotificationChannel()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        adapter = TaskAdapter (
            onCheckboxClick = { task, isChecked ->
                taskViewModel.updateTaskCompletion(task, isChecked)
            },
            onEditClick = { task -> showEditTaskDialog(task) }
        )

        binding.recyclerViewTasks.apply {
            adapter = this@MainActivity.adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        taskViewModel.allTasks.observe(this) { tasks ->
            if (tasks.isNullOrEmpty()) {
                adapter.submitList(emptyList()) // Явно передаем пустой список
            } else {
                adapter.submitList(tasks)
            }
        }


        setupButton()

        setupSwipeToDelete()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    102
                )
            }
        }
        if (checkNotificationPermission()) {
            NotificationHelper.showInputNotification(this)
        }
    }


    private fun setupButton() {
        binding.btnNew.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun setupSwipeToDelete() {
        val swipeHandler = object :ItemTouchHelper.SimpleCallback(
            0, // Не используем drag&drop
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // Свайп в обе стороны
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false // Не поддерживаем перемещение

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                taskViewModel.delete(adapter.currentList[position])
            }
        }

        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.recyclerViewTasks)
    }

    private fun showAddTaskDialog() {
        val editText = EditText(this).apply {
            hint = "Введите задачу..."
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12.dpToPx(), 0.dpToPx(), 12.dpToPx(), 0.dpToPx())
            addView(editText)
        }

        AlertDialog.Builder(this, R.style.MyAlertDialogTheme)
            .setTitle("Добавить задачу")
            .setView(container)
            .setPositiveButton("Добавить") { _, _ ->
                editText.text.toString().takeIf { it.isNotEmpty() }?.let { text ->
                    if (text.isNotBlank()) {
                        // Создание и сохранение задачи
                        val task = Task(text = text)
                        taskViewModel.insert(task)
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEditTaskDialog(task: Task) {
        val editText = EditText(this).apply {
            setText(task.text)
            hint = "Редактировать задачу"
            setSelection(text.length) // Курсор в конец
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12.dpToPx(), 0.dpToPx(), 12.dpToPx(), 0)
            addView(editText)
        }

        AlertDialog.Builder(this, R.style.MyAlertDialogTheme)
            .setTitle("Редактировать задачу")
            .setView(container)
            .setPositiveButton("Сохранить") { _, _ ->
                val newText = editText.text.toString()
                if (newText.isNotBlank()) {
                    // Создаем обновленную задачу
                    val updatedTask = task.copy(text = newText)
                    taskViewModel.update(updatedTask)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "task_channel_id",
                "Добавление задач",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Канал для быстрого добавления задач"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }


}

