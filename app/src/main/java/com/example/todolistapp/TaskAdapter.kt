package com.example.todolistapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.databinding.ItemTaskBinding


class TaskAdapter(
    private val onDeleteClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var tasks = listOf<Task>()

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.tvTaskText.text = task.text
            binding.btnDelete.setOnClickListener {
                onDeleteClick(task)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount() = tasks.size
}
