package org.wit.tazq_app.models

import timber.log.Timber

private var lastId = 0L

private fun getId(): Long {
    return lastId++
}

class TaskMemStore : TaskStore {
    private val tasks = ArrayList<TaskModel>()

    override fun findAll(): List<TaskModel> {
        logAll()
        return tasks
    }

    override fun findById(id: Long): TaskModel? {
        return tasks.find { it.id == id }
    }

    override fun create(task: TaskModel) {
        task.id = getId()
        tasks.add(task)
        logAll()
    }

    override fun update(task: TaskModel) {
        val foundTask = findById(task.id)
        if (foundTask != null) {
            foundTask.title = task.title
            foundTask.description = task.description
            foundTask.isCompleted = task.isCompleted
            foundTask.dueDate = task.dueDate
            foundTask.image = task.image
            foundTask.location = task.location
            logAll()
        }
    }

    override fun delete(task: TaskModel) {
        tasks.remove(task)
        logAll()
    }

    private fun logAll() {
        Timber.i("Task Store Contents:")
        tasks.forEach { Timber.i("Task: $it") }
    }
}