package com.udacity

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udacity.utils.downloadFile
import com.udacity.utils.isNetworkAvailable
import com.udacity.utils.isUrlValid
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var selectedRepo: String? = null
    private var selectedOption: String? = null

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
                                sendNotification(title, "Success", context!!)
                                cursor.close()
                            } else {
                               custom_button.setDownloadButtonState(ButtonState.Completed)
                                sendNotification(title, "Fail", context!!)
                                cursor.close()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun download() {
        if(isNetworkAvailable(this)) {
            when {
                custom_url_edit_text.text.toString().isNotEmpty() -> {
                    downloadFromCustomUrl(custom_url_edit_text.text.toString())
                }
                selectedRepo != null -> {
                    downloadFromChosenUrl()
                }
                else -> {
                    custom_button.setDownloadButtonState(ButtonState.Completed)
                    Toast.makeText(this, R.string.no_selection_hint, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, R.string.network_unavailable_hint, Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadFromChosenUrl() {
        custom_button.setDownloadButtonState(ButtonState.Loading)

        val request = downloadFile(selectedOption!!, selectedRepo!!, this)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun downloadFromCustomUrl(url: String) {
        if (isUrlValid(url.toLowerCase(Locale.ROOT))) {
            custom_button.setDownloadButtonState(ButtonState.Loading)

            val request = downloadFile(getString(R.string.custom_file), url, this)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                    downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        } else {
            Toast.makeText(this, getString(R.string.url_not_valid_hint), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val UDACITY =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val RETROFIT =
            "https://github.com/square/retrofit/archive/master.zip"
    }
}
