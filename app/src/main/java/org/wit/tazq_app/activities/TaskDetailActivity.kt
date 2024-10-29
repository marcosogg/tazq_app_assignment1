// /app/src/main/java/org/wit/tazq_app/activities/TaskDetailActivity.kt

package org.wit.tazq_app.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.wit.tazq_app.databinding.ActivityTaskDetailBinding
import org.wit.tazq_app.models.TaskModel
import timber.log.Timber.i
import java.text.SimpleDateFormat
import java.util.Locale

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDetailBinding
    private var task: TaskModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "Task Details"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        task = intent.getParcelableExtra("task_detail")
        task?.let { displayTaskDetails(it) } ?: run {
            i("No task details provided")
            finish()
        }
    }

    private fun displayTaskDetails(task: TaskModel) {
        binding.tvDetailTitle.text = task.title
        binding.tvDetailDescription.text = task.description
        binding.cbDetailCompleted.isChecked = task.isCompleted

        task.dueDate?.let { timestamp ->
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            binding.tvDetailDueDate.text = sdf.format(java.util.Date(timestamp))
        } ?: run {
            binding.tvDetailDueDate.text = "No Due Date"
        }

        binding.cbDetailCompleted.setOnCheckedChangeListener { _, isChecked ->
            task?.isCompleted = isChecked
            // Optionally, you can update the task in the store here
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
