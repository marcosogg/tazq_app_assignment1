package org.wit.tazq_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import org.wit.tazq_app.activities.TaskActivity

class SplashActivity : AppCompatActivity() {

    private val splashTimeOut: Long = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            // Start the main activity
            val intent = Intent(this@SplashActivity, TaskActivity::class.java)
            startActivity(intent)
            finish()
        }, splashTimeOut)
    }
}
