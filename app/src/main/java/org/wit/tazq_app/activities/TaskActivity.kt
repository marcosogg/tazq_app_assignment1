package org.wit.tazq_app.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.wit.tazq_app.R
import org.wit.tazq_app.databinding.ActivityTaskBinding
import org.wit.tazq_app.main.MainApp
import org.wit.tazq_app.models.TaskModel
import timber.log.Timber.i

class TaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskBinding
    var task = TaskModel()
    lateinit var app: MainApp
    var edit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        app = application as MainApp

        if (intent.hasExtra("task_edit")) {
            edit = true
            task = intent.extras?.getParcelable("task_edit")!!
            binding.taskTitle.setText(task.title)
            binding.taskDescription.setText(task.description)
            binding.btnAdd.text = getString(R.string.button_saveTask)
        }

        binding.btnAdd.setOnClickListener { view ->
            val titleInput = binding.taskTitle.text.toString().trim()
            val descriptionInput = binding.taskDescription.text.toString().trim()
            val dueDateInput = binding.taskDueDate.text.toString().trim()

            var isValid = true

            // Validate Title
            if (titleInput.isEmpty()) {
                binding.tilTaskTitle.error = "Please enter a task title"
                isValid = false
            } else if (titleInput.length > 50) {
                binding.tilTaskTitle.error = "Title cannot exceed 50 characters"
                isValid = false
            } else {
                binding.tilTaskTitle.error = null
            }

            // Validate Description
            if (descriptionInput.length > 200) {
                binding.tilTaskDescription.error = "Description cannot exceed 200 characters"
                isValid = false
            } else {
                binding.tilTaskDescription.error = null
            }

            // Validate Due Date (if implemented)
            // Assuming dueDate is input as a timestamp or specific format
            // For simplicity, we'll skip this unless you have a date picker
            // If dueDate is set via a DatePicker, ensure it's a valid future date

            if (isValid) {
                task.title = titleInput
                task.description = descriptionInput
                // Parse and set dueDate if implemented
                // task.dueDate = parsedDate

                if (edit) {
                    app.tasks.update(task.copy())
                    i("Task updated: $task")
                } else {
                    app.tasks.create(task.copy())
                    i("Task created: $task")
                }

                setResult(RESULT_OK)
                finish()
            } else {
                Snackbar.make(view, "Please correct the errors above", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
