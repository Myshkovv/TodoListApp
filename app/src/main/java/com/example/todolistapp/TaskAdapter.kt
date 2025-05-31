package com.example.todolistapp

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.databinding.ItemTaskBinding


class TaskAdapter(
    private val onCheckboxClick: (Task, Boolean) -> Unit,
    private val onEditClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    var currentList: List<Task> = emptyList()
        private set

    private var tasks = listOf<Task>()

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.tvTaskText.text = task.text

            binding.checkBoxCompleted.setOnCheckedChangeListener(null)

            binding.checkBoxCompleted.isChecked = task.isCompleted
            updateTextAppearance(binding.tvTaskText, task.isCompleted)

            binding.checkBoxCompleted.setOnCheckedChangeListener { _, isChecked ->
                if (binding.checkBoxCompleted.isPressed) {
                    onCheckboxClick(task, isChecked)
                }
            }

            itemView.setOnLongClickListener {
                onEditClick(task)
                true
            }

            itemView.setOnLongClickListener {
                itemView.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction {
                        itemView.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start()
                    }
                    .start()

                onEditClick(task)
                true
            }


        }
        private fun updateTextAppearance(textView: TextView, isCompleted: Boolean) {
            textView.apply {
                paintFlags = if (isCompleted) {
                    paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                alpha = if (isCompleted) 0.6f else 1.0f
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<Task>) {
        currentList = newList.sortedWith(
            compareBy<Task> { it.isCompleted }.thenByDescending { it.createdAt }
        )
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
        holder.bind(currentList[position])
    }

    override fun getItemCount() = currentList.size
}
