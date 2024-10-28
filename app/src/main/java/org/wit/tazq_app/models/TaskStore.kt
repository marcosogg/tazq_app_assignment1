package org.wit.tazq_app.models

interface TaskStore {
    fun findAll(): List<TaskModel>
    fun create(task: TaskModel)
    fun update(task: TaskModel)
    fun delete(task: TaskModel)
    fun findById(id: Long): TaskModel?
}