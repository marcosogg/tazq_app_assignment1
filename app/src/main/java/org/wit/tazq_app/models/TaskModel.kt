package org.wit.tazq_app.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskModel(
    var id: Long = 0,
    var title: String = "",
    var description: String = "",
    var isCompleted: Boolean = false,
    var dueDate: Long? = null
) : Parcelable