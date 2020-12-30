package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.utils.NOTIFICATION_ID
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val notificationManager = ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
        ) as NotificationManager

        notificationManager.cancel(NOTIFICATION_ID)

        val fileName = intent.getStringExtra("fileName")
        val downloadStatus = intent.getStringExtra("downloadStatus")

        when(fileName) {
            "LoadApp" -> file_name_value.text = getString(R.string.load_app_text)
            "Glide" -> file_name_value.text = getString(R.string.glide_text)
            "Retrofit" -> file_name_value.text = getString(R.string.retrofit_text)
        }

        when(downloadStatus) {
            "Fail" -> download_status_value.setTextColor(Color.RED)
            "Success" -> download_status_value.setTextColor(getColor(R.color.colorPrimaryDark))
        }

        download_status_value.text = downloadStatus

        ok_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
