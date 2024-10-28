package org.wit.tazq_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.wit.tazq_app.databinding.ActivityTaskBinding
import timber.log.Timber
import timber.log.Timber.i

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())
        i("Task Activity started..")

        binding.btnAdd.setOnClickListener() {
            i("add Button Pressed")
        }
    }
}