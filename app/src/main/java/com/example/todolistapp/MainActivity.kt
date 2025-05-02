package com.example.todolistapp

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityLearnWordBinding most not be null")
    private lateinit var taskViewModel: TaskViewModel // 1. ViewModel
    private lateinit var adapter: TaskAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        adapter = TaskAdapter { task ->
            taskViewModel.delete(task) // 4. Удаление через ViewModel
        }

        binding.recyclerViewTasks.adapter = adapter
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)

        taskViewModel.allTasks.observe(this) { tasks ->
            adapter.submitList(tasks) // 6. Автоматическое обновление
        }


        setupButton()
    }


    private fun setupButton() {
        binding.btnNew.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun showAddTaskDialog() {
        val editText = EditText(this).apply {
            hint = "Введите задачу..."
        }
        AlertDialog.Builder(this)
            .setTitle("Добавить задачу")
            .setView(editText)
            .setPositiveButton("Добавить") { _, _ ->
                editText.text.toString().takeIf { it.isNotEmpty() }?.let { text ->
                    if (text.isNotBlank()) {
                        // 8. Создание и сохранение задачи
                        val task = Task(text = text)
                        taskViewModel.insert(task)
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }




    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}