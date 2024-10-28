package org.wit.tazq_app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.tazq_app.R
import org.wit.tazq_app.adapters.TaskAdapter
import org.wit.tazq_app.adapters.TaskListener
import org.wit.tazq_app.databinding.ActivityTaskListBinding
import org.wit.tazq_app.main.MainApp
import org.wit.tazq_app.models.TaskModel

class TaskListActivity : AppCompatActivity(), TaskListener {

    private lateinit var binding: ActivityTaskListBinding
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = TaskAdapter(app.tasks.findAll(), this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, TaskActivity::class.java)
                getResult.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                binding.recyclerView.adapter?.notifyItemRangeChanged(0, app.tasks.findAll().size)
            }
        }

    override fun onTaskClick(task: TaskModel) {
        val launcherIntent = Intent(this, TaskActivity::class.java)
        launcherIntent.putExtra("task_edit", task)
        getClickResult.launch(launcherIntent)
    }

    override fun onTaskCheckChanged(task: TaskModel, isChecked: Boolean) {
        task.isCompleted = isChecked
        app.tasks.update(task)
    }

    private val getClickResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                binding.recyclerView.adapter?.notifyItemRangeChanged(0, app.tasks.findAll().size)
            }
        }
}