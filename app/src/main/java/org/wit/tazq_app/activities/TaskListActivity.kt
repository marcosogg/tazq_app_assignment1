package org.wit.tazq_app.activities

import android.app.Activity
import android.app.AlertDialog
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
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "Tasks"
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        setupSpinner()
        setupFab()
        setupRecyclerView()
        loadTasks()
    }

    /**
     * Sets up the filter spinner with options and listeners.
     */
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

    /**
     * Sets up the FloatingActionButton to add new tasks.
     */
    private fun setupFab() {
        val fab: FloatingActionButton = binding.fabAddTask
        fab.setOnClickListener {
            val launcherIntent = Intent(this, TaskActivity::class.java)
            getResult.launch(launcherIntent)
        }
    }

    /**
     * Initializes the RecyclerView and its adapter.
     */
    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(filteredTasks, this)
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = taskAdapter
    }

    /**
     * Loads all tasks from the data store and applies the current filter.
     */
    private fun loadTasks() {
        allTasks = app.tasks.findAll()
        val selectedFilter = binding.spinnerFilter.selectedItem?.toString() ?: "All"
        filterTasks(selectedFilter)
    }

    /**
     * Filters tasks based on the selected criteria.
     *
     * @param filter The filter criteria ("All", "Completed", "Pending", "Overdue").
     */
    private fun filterTasks(filter: String) {
        filteredTasks = when (filter) {
            "Completed" -> allTasks.filter { it.isCompleted }
            "Pending" -> allTasks.filter { !it.isCompleted }
            "Overdue" -> allTasks.filter {
                it.dueDate != null && it.dueDate!! < System.currentTimeMillis() && !it.isCompleted
            }
            else -> allTasks
        }
        updateRecyclerView()
    }

    /**
     * Updates the RecyclerView with the latest filtered tasks.
     */
    private fun updateRecyclerView() {
        taskAdapter.updateTasks(filteredTasks)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Handles menu item selections, including "Add Task" and "Delete All Tasks".
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_delete_all -> {
                showDeleteAllConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Displays a confirmation dialog before deleting all tasks.
     */
    private fun showDeleteAllConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete All Tasks")
            .setMessage("Are you sure you want to delete all tasks?")
            .setPositiveButton("Yes") { dialog, _ ->
                app.tasks.deleteAll()
                loadTasks()
                Snackbar.make(binding.root, "All tasks have been deleted", Snackbar.LENGTH_LONG).show()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    /**
     * Registers for the activity result when adding a new task.
     */
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadTasks()
            }
        }

    /**
     * Handles task item clicks to navigate to the TaskDetailActivity.
     *
     * @param task The TaskModel that was clicked.
     */
    override fun onTaskClick(task: TaskModel) {
        val launcherIntent = Intent(this, TaskDetailActivity::class.java)
        launcherIntent.putExtra("task_detail", task)
        startActivity(launcherIntent)
    }

    /**
     * Handles changes in the task completion checkbox.
     *
     * @param task The TaskModel that was checked/unchecked.
     * @param isChecked The new checked state.
     */
    override fun onTaskCheckChanged(task: TaskModel, isChecked: Boolean) {
        task.isCompleted = isChecked
        app.tasks.update(task)
        loadTasks()
    }
}
