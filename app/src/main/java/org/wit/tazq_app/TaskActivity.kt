// /app/src/main/java/org/wit/tazq_app/TaskActivity.kt
package org.wit.tazq_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.wit.tazq_app.databinding.ActivityTaskBinding
import timber.log.Timber
import timber.log.Timber.i

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskBinding
    var task = TaskModel()
    val tasks = ArrayList<TaskModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())
        i("Task Activity started...")

        binding.btnAdd.setOnClickListener { view ->
            task.title = binding.taskTitle.text.toString()
            task.description = binding.description.text.toString()

            if (task.title.isNotEmpty()) {
                tasks.add(task.copy()) // Use copy() to add a new instance
                i("Add Button Pressed: ${task.copy()}")
                for (i in tasks.indices) {
                    i("Task[$i]: ${tasks[i]}")
                }
                // Optionally, clear the input fields after adding
                binding.taskTitle.text.clear()
                binding.description.text.clear()
            } else {
                Snackbar.make(view, "Please Enter a title", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }
}
