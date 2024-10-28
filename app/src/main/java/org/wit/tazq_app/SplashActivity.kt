package org.wit.tazq_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import org.wit.tazq_app.activities.TaskListActivity

class SplashActivity : AppCompatActivity() {

    private val splashTimeOut: Long = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            // Start the TaskListActivity
            val intent = Intent(this@SplashActivity, TaskListActivity::class.java)
            startActivity(intent)
            finish()
        }, splashTimeOut)
    }
}