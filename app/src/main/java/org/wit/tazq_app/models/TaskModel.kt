package org.wit.tazq_app.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class TaskModel(
    var id: Long = 0,
    var title: String = "",
    var description: String = "",
    var isCompleted: Boolean = false,
    var dueDate: Long? = null,
    var image: String = "",
    var location: Location = Location(),
    var priority: Priority = Priority.MEDIUM,
    var tags: MutableList<String> = mutableListOf(),
    var createdAt: Long = Date().time,
    var updatedAt: Long = Date().time
) : Parcelable {

    fun isOverdue(): Boolean {
        return dueDate?.let { it < Date().time } ?: false
    }

    fun hasLocation(): Boolean {
        return location.lat != 0.0 && location.lng != 0.0
    }

    enum class Priority {
        LOW, MEDIUM, HIGH
    }
}