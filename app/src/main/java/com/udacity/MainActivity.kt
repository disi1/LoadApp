package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var selectedRepo: String? = null
    private var selectedOption: String? = null

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        download_options_radio_group.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.glide_radio_button -> {
                    selectedRepo = GLIDE
                    selectedOption = "Glide"
                }
                R.id.udacity_radio_button -> {
                    selectedRepo = UDACITY
                    selectedOption = "LoadApp"
                }

                R.id.retrofit_radio_button -> {
                    selectedRepo = RETROFIT
                    selectedOption = "Retrofit"
                }
            }
        }

        custom_button.setOnClickListener {
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val action = intent?.action

            if(downloadID == id) {
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    val query = DownloadManager.Query().setFilterById(downloadID)
                    val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val cursor = downloadManager.query(query)
                    if (cursor.moveToFirst()) {
                        if (cursor.count > 0) {
                            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            val title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                               custom_button.setDownloadButtonState(ButtonState.Completed)
                                cursor.close()
                            } else {
                               custom_button.setDownloadButtonState(ButtonState.Completed)
                                cursor.close()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun download() {
        if(selectedRepo != null) {
            custom_button.setDownloadButtonState(ButtonState.Loading)

            val request =
                    DownloadManager.Request(Uri.parse(selectedRepo))
                            .setTitle(selectedOption)
                            .setDescription(getString(R.string.app_description))
                            .setRequiresCharging(false)
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                    downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        } else {
            custom_button.setDownloadButtonState(ButtonState.Completed)
            Toast.makeText(this, R.string.no_selection_hint, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val UDACITY =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val RETROFIT =
            "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
