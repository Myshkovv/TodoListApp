package com.example.todolistapp

import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginStart
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityLearnWordBinding most not be null")
    private val tasks = mutableListOf<String>()
    private lateinit var adapter: TaskAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupRecyclerView()
        setupButton()

    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(tasks) { position ->
            tasks.removeAt(position)
            adapter.notifyItemRemoved(position)
        }
        binding.recyclerViewTasks.adapter = adapter
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)
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
                editText.text.toString().takeIf { it.isNotEmpty() }?.let { task ->
                    tasks.add(task)
                    adapter.notifyItemInserted(tasks.size - 1)
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