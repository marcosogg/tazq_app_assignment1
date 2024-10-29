package org.wit.tazq_app.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.wit.tazq_app.R
import org.wit.tazq_app.databinding.ActivityTaskBinding
import org.wit.tazq_app.helpers.showImagePicker
import org.wit.tazq_app.main.MainApp
import org.wit.tazq_app.models.TaskModel
import timber.log.Timber.i

class TaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskBinding
    private lateinit var imageIntentLauncher: ActivityResultLauncher<Intent>
    var task = TaskModel()
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var edit = false
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)
        app = application as MainApp

        registerImagePickerCallback()

        if (intent.hasExtra("task_edit")) {
            edit = true
            task = intent.extras?.getParcelable("task_edit")!!
            binding.taskTitle.setText(task.title)
            binding.taskDescription.setText(task.description)
            binding.btnAdd.setText(R.string.save_task)
            if (task.image != Uri.EMPTY) {
                Picasso.get()
                    .load(task.image)
                    .into(binding.taskImage)
                binding.chooseImage.setText(R.string.change_task_image)
            }
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

        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }

        i("Task Activity started...")
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
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Image Result ${result.data!!.data}")
                            val image = result.data!!.data!!
                            contentResolver.takePersistableUriPermission(
                                image,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                            task.image = image
                            Picasso.get()
                                .load(task.image)
                                .into(binding.taskImage)
                            binding.chooseImage.setText(R.string.change_task_image)
                        }
                    }
                    RESULT_CANCELED -> {
                        i("Image picker cancelled")
                    }
                    else -> {
                        i("Image picker failed")
                    }
                }
            }
    }
}