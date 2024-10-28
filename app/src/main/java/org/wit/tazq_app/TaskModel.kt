package org.wit.tazq_app

data class TaskModel(
    var title: String = "",
    var description: String = ""
) {
    // Optionally, override toString() for better logging readability
    override fun toString(): String {
        return "Task(title='$title', description='$description')"
    }
}
