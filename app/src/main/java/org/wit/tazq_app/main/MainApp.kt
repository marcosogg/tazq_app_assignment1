package org.wit.tazq_app.main

import android.app.Application
import org.wit.tazq_app.models.TaskJSONStore
import org.wit.tazq_app.models.TaskStore
import timber.log.Timber

class MainApp : Application() {

    lateinit var tasks: TaskStore

    override fun onCreate() {
        super.onCreate()
        tasks = TaskJSONStore(applicationContext)
        Timber.plant(Timber.DebugTree())
        Timber.i("TAZQ App started")
    }
}