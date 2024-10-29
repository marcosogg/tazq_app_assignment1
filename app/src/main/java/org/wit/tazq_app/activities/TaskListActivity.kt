package org.wit.tazq_app.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.wit.tazq_app.R
import org.wit.tazq_app.adapters.TaskAdapter
import org.wit.tazq_app.adapters.TaskListener
import org.wit.tazq_app.databinding.ActivityTaskListBinding
import org.wit.tazq_app.models.TaskModel
import org.wit.tazq_app.main.MainApp

class TaskListActivity : AppCompatActivity(), TaskListener {

    private lateinit var binding: ActivityTaskListBinding
    lateinit var app: MainApp
    private var allTasks: List<TaskModel> = listOf()
    private var filteredTasks: List<TaskModel> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "Tasks"
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        setupSpinner()
        setupFab()

        loadTasks()
    }

    private fun setupSpinner() {
        val spinner = binding.spinnerFilter
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.task_filter_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> filterTasks("All")
                    1 -> filterTasks("Completed")
                    2 -> filterTasks("Pending")
                    3 -> filterTasks("Overdue")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                filterTasks("All")
            }
        }
    }

    private fun setupFab() {
        val fab: FloatingActionButton = binding.fabAddTask
        fab.setOnClickListener {
            val launcherIntent = Intent(this, TaskActivity::class.java)
            getResult.launch(launcherIntent)
        }
    }

    private fun loadTasks() {
        allTasks = app.tasks.findAll()
        filterTasks("All")
    }

    private fun filterTasks(filter: String) {
        filteredTasks = when (filter) {
            "Completed" -> allTasks.filter { it.isCompleted }
            "Pending" -> allTasks.filter { !it.isCompleted }
            "Overdue" -> allTasks.filter { it.dueDate != null && it.dueDate!! < System.currentTimeMillis() && !it.isCompleted }
            else -> allTasks
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = TaskAdapter(filteredTasks, this)
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
                loadTasks()
            }
        }

    override fun onTaskClick(task: TaskModel) {
        val launcherIntent = Intent(this, TaskDetailActivity::class.java)
        launcherIntent.putExtra("task_detail", task)
        startActivity(launcherIntent)
    }

    override fun onTaskCheckChanged(task: TaskModel, isChecked: Boolean) {
        task.isCompleted = isChecked
        app.tasks.update(task)
        loadTasks()
    }
}
