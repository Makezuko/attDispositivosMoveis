package com.example.login_simulation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.login_simulation.databinding.ItemTaskBinding

class TaskAdapter(
    private val tasks: List<Task>,
    private val onTaskClicked: ((Task) -> Unit)? = null
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.tvTitle.text = task.title
            binding.tvCreatedBy.text = "Criado por: ${task.createdBy ?: "-"}"
            binding.tvModifiedBy.text = "Modificado por: ${task.modifiedBy ?: "-"}"

            binding.root.setOnClickListener {
                onTaskClicked?.invoke(task)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size
}
