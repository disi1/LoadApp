package com.udacity.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.udacity.DetailActivity
import com.udacity.R

const val NOTIFICATION_ID = 0

fun sendNotification(fileName: String, downloadStatus: String, context: Context) {
    // Make a channel if necessary
    createChannel(
            context,
            context.getString(R.string.repo_download_channel_id),
            context.getString(R.string.repo_download_channel_name),
            context.getString(R.string.repo_download_channel_description))

    val contentIntent = Intent(context, DetailActivity::class.java)
    contentIntent.putExtra("fileName", fileName)
    contentIntent.putExtra("downloadStatus", downloadStatus)
    val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_ONE_SHOT
    )

    // Create the notification
    var notificationDescription = ""
    when(fileName) {
        context.getString(R.string.custom_file) -> {
            when (downloadStatus) {
                "Success" -> notificationDescription = context.getString(R.string.custom_file_notification_description, fileName, "complete")
                "Fail" -> notificationDescription = context.getString(R.string.custom_file_notification_description, fileName, "failed")
            }
        } else -> {
        when(downloadStatus) {
            "Success" -> notificationDescription = context.getString(R.string.notification_description, fileName, "complete")
            "Fail" -> notificationDescription = context.getString(R.string.notification_description, fileName, "failed")
            }
        }
    }

    val successLargeIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_thumb_up_24)
    val failureLargeIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_thumb_down_24)
    val statusImage = when(downloadStatus) {
        "Success" -> drawableToBitmap(
                successLargeIcon!!
        )
        else -> drawableToBitmap(
                failureLargeIcon!!
        )
    }
    val bigPictureStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(statusImage)
        .bigLargeIcon(null)

    val builder = NotificationCompat.Builder(
            context,
            context.getString(R.string.repo_download_channel_id)
    )
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(notificationDescription)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setStyle(bigPictureStyle)
            .setLargeIcon(statusImage)
            .addAction(
                    R.drawable.ic_assistant_black_24dp,
                    context.getString(R.string.check_status_button),
                    contentPendingIntent
            )

    // Show the notification
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
}

private fun createChannel(context: Context, channelId: String, channelName: String, channelDescription: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
        )
                .apply {
                    setShowBadge(false)
                }

        notificationChannel.description = channelDescription

        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

    }
}

fun drawableToBitmap(drawable: Drawable): Bitmap? {
    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }
    val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}