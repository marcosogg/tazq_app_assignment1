package org.wit.tazq_app.main

import android.app.Application
import org.wit.tazq_app.models.TaskMemStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    lateinit var tasks: TaskMemStore

    override fun onCreate() {
        super.onCreate()
        tasks = TaskMemStore()
        Timber.plant(Timber.DebugTree())
        i("TAZQ App started")
    }
}