package org.wit.tazq_app.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.wit.tazq_app.R
import org.wit.tazq_app.databinding.ActivityTaskBinding
import org.wit.tazq_app.main.MainApp
import org.wit.tazq_app.TaskModel
import timber.log.Timber.i

class TaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskBinding
    private lateinit var app: MainApp
    var task = TaskModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp
        i("Task Activity started...")

        binding.btnAdd.setOnClickListener { view ->
            task.title = binding.taskTitle.text.toString()
            task.description = binding.description.text.toString()

            if (task.title.isNotEmpty()) {
                app.tasks.add(task.copy())
                i("Add Button Pressed: ${task}")
                setResult(RESULT_OK)
                finish()
            } else {
                Snackbar.make(view, "Please Enter a title", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
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