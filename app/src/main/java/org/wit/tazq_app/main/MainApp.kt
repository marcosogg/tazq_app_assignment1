package org.wit.tazq_app.main

import android.app.Application
import org.wit.tazq_app.TaskModel
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {
    val tasks = ArrayList<TaskModel>()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("TAZQ App started")
    }
}