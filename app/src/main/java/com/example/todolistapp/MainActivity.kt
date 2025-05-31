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
    private val binding get() = _binding!!

    private lateinit var taskViewModel: TaskViewModel // 1. ViewModel
    private lateinit var adapter: TaskAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.main)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        adapter = TaskAdapter { task ->
            taskViewModel.delete(task)
        }

        binding.recyclerViewTasks.apply {
            adapter = this@MainActivity.adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        taskViewModel.allTasks.observe(this) { tasks ->
            adapter.submitList(tasks)
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