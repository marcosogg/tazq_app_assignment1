package org.wit.tazq_app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import org.wit.tazq_app.R
import org.wit.tazq_app.databinding.ActivityTaskBinding
import org.wit.tazq_app.main.MainApp
import org.wit.tazq_app.models.Location
import org.wit.tazq_app.models.TaskModel
import timber.log.Timber.i

class TaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskBinding
    var task = TaskModel()
    lateinit var app: MainApp
    var edit = false
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)
        app = application as MainApp

        if (intent.hasExtra("task_edit")) {
            edit = true
            task = intent.extras?.getParcelable("task_edit")!!
            binding.taskTitle.setText(task.title)
            binding.taskDescription.setText(task.description)
            binding.btnAdd.setText(R.string.button_saveTask)
        }

        binding.btnAdd.setOnClickListener() {
            task.title = binding.taskTitle.text.toString()
            task.description = binding.taskDescription.text.toString()
            if (task.title.isEmpty()) {
                Snackbar.make(it, R.string.enter_task_title, Snackbar.LENGTH_LONG)
                    .show()
            } else {
                if (edit) {
                    app.tasks.update(task.copy())
                } else {
                    app.tasks.create(task.copy())
                }
                setResult(RESULT_OK)
                finish()
            }
        }

        binding.taskLocation.setOnClickListener {
            val location = Location(52.245696, -7.139102, 15f)
            if (task.location.zoom != 0f) {
                location.lat = task.location.lat
                location.lng = task.location.lng
                location.zoom = task.location.zoom
            }
            val launcherIntent = Intent(this, MapActivity::class.java)
                .putExtra("location", location)
            mapIntentLauncher.launch(launcherIntent)
        }

        binding.taskDueDate.setOnClickListener {
            showDatePicker()
        }

        registerMapCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> { finish() }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode) {
                    Activity.RESULT_OK -> {
                        if (result.data != null) {
                            val location = result.data!!.extras?.getParcelable<Location>("location")!!
                            task.location = location
                            i("Location: $location")
                        }
                    }
                    Activity.RESULT_CANCELED -> { }
                    else -> { }
                }
            }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Due Date")
            .setSelection(task.dueDate ?: MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            task.dueDate = selection
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }
}