package org.wit.tazq_app.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.wit.tazq_app.R
import org.wit.tazq_app.databinding.ActivityTaskBinding
import org.wit.tazq_app.helpers.showImagePicker
import org.wit.tazq_app.main.MainApp
import org.wit.tazq_app.models.Location
import org.wit.tazq_app.models.TaskModel
import timber.log.Timber

class TaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskBinding
    private lateinit var imageIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>
    var task = TaskModel()
    lateinit var app: MainApp
    var edit = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val DEFAULT_LAT = 52.245696
        private const val DEFAULT_LNG = -7.139102
        private const val DEFAULT_ZOOM = 15f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        // Check if we're editing an existing task
        if (intent.hasExtra("task_edit")) {
            edit = true
            task = intent.extras?.getParcelable("task_edit")!!
            binding.taskTitle.setText(task.title)
            binding.taskDescription.setText(task.description)
            binding.btnAdd.setText(R.string.button_saveTask)
            if (task.image.isNotEmpty()) {
                Picasso.get()
                    .load(task.image)
                    .into(binding.taskImage)
            }
        }

        // Save button click handler
        binding.btnAdd.setOnClickListener {
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

        // Location button click handler
        binding.taskLocation.setOnClickListener {
            if (checkLocationPermissions()) {
                launchMapWithLocation()
            }
        }

        // Due date button click handler
        binding.taskDueDate.setOnClickListener {
            showDatePicker()
        }

        // Image button click handler
        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }

        registerImagePickerCallback()
        registerMapCallback()
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

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when(result.resultCode) {
                    Activity.RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Result ${result.data!!.data}")
                            task.image = result.data!!.data!!.toString()
                            Picasso.get()
                                .load(task.image)
                                .into(binding.taskImage)
                        }
                    }
                    Activity.RESULT_CANCELED -> { }
                    else -> { }
                }
            }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when(result.resultCode) {
                    Activity.RESULT_OK -> {
                        if (result.data != null) {
                            val location = result.data!!.extras?.getParcelable<Location>("location")!!
                            task.location = location
                            Timber.i("Location: $location")
                        }
                    }
                    Activity.RESULT_CANCELED -> { }
                    else -> { }
                }
            }
    }

    private fun checkLocationPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchMapWithLocation()
                } else {
                    Snackbar.make(
                        binding.root,
                        R.string.location_permission_required,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun launchMapWithLocation() {
        val location = Location(DEFAULT_LAT, DEFAULT_LNG, DEFAULT_ZOOM)
        if (task.location.zoom != 0f) {
            location.lat = task.location.lat
            location.lng = task.location.lng
            location.zoom = task.location.zoom
        }
        val launcherIntent = Intent(this, MapActivity::class.java)
            .putExtra("location", location)
        mapIntentLauncher.launch(launcherIntent)
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_task_due_date))
            .setSelection(task.dueDate ?: MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            task.dueDate = selection
            updateDueDateDisplay()
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun updateDueDateDisplay() {
        // Update the due date button text with the selected date
        task.dueDate?.let { timestamp ->
            val date = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                .format(java.util.Date(timestamp))
            binding.taskDueDate.text = getString(R.string.due_date_display, date)
        }
    }
}