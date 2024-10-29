package org.wit.tazq_app.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.wit.tazq_app.helpers.*
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val JSON_FILE = "tasks.json"

class TaskJSONStore(private val context: Context) : TaskStore {
    private var tasks = mutableListOf<TaskModel>()
    private val gsonBuilder: Gson = GsonBuilder()
        .registerTypeAdapter(Uri::class.java, UriParser())
        .create()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): List<TaskModel> {
        logAll()
        return tasks
    }

    override fun findById(id: Long): TaskModel? {
        return tasks.find { it.id == id }
    }

    override fun create(task: TaskModel) {
        task.id = generateRandomId()
        task.createdAt = Date().time
        task.updatedAt = task.createdAt
        tasks.add(task)
        serialize()
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
            foundTask.priority = task.priority
            foundTask.tags = task.tags
            foundTask.updatedAt = Date().time
            serialize()
        }
    }

    override fun delete(task: TaskModel) {
        tasks.remove(task)
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(tasks)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        val collectionType: Type = object : TypeToken<List<TaskModel>>() {}.type
        tasks = gsonBuilder.fromJson(jsonString, collectionType)
    }

    private fun logAll() {
        tasks.forEach { Timber.i("Task: $it") }
    }

    private fun generateRandomId(): Long {
        return Random().nextLong()
    }

    class UriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): Uri {
            return Uri.parse(json?.asString)
        }

        override fun serialize(
            src: Uri?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return JsonPrimitive(src.toString())
        }
    }
}