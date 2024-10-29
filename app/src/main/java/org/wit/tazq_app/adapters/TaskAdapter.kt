package org.wit.tazq_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.tazq_app.databinding.CardTaskBinding
import org.wit.tazq_app.models.TaskModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface TaskListener {
    fun onTaskClick(task: TaskModel)
    fun onTaskCheckChanged(task: TaskModel, isChecked: Boolean)
}

class TaskAdapter(
    private var tasks: List<TaskModel>,
    private val listener: TaskListener
) : RecyclerView.Adapter<TaskAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val task = tasks[holder.adapterPosition]
        holder.bind(task, listener)
    }

    override fun getItemCount(): Int = tasks.size

    class MainHolder(private val binding: CardTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: TaskModel, listener: TaskListener) {
            binding.apply {
                tvTaskTitle.text = task.title
                tvTaskDescription.text = task.description
                cbTaskCompleted.isChecked = task.isCompleted

                task.dueDate?.let { timestamp ->
                    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    tvTaskDueDate.text = sdf.format(Date(timestamp))
                } ?: run {
                    tvTaskDueDate.text = "No Due Date" // No need for View import
                }

                root.setOnClickListener { listener.onTaskClick(task) }
                cbTaskCompleted.setOnCheckedChangeListener { _, isChecked ->
                    listener.onTaskCheckChanged(task, isChecked)
                }
            }
        }
    }
}
