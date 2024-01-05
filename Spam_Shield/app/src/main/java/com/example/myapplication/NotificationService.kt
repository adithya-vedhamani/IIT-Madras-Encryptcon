package com.example.myapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.myapplication.helpers.TextClassificationClient
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.RemoteMessage.Notification
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

private lateinit var scamModel: TextClassificationClient


const val channelid = "notification_channel"
const val channelname = "com.example.myapplication"

class NotificationService: FirebaseMessagingService() {
    // Generate notifications
    // Attach the notification created with custom layout
    // Show the notification

    @RequiresApi(Build.VERSION_CODES.O)
    // Analyze incoming notifications and show a dialog box if they are classified as scams
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.getNotification() != null){
            val notificationText = remoteMessage.notification!!.body!!
            val score = scamModel.classify(notificationText)

            if (score > 0.9) {
                val builder = AlertDialog.Builder(applicationContext)
                builder.setMessage("This notification is spam.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                generateNotification(remoteMessage.notification!!.title!!, notificationText)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        // Load the NLP model
        scamModel.load()
    }

    

    @SuppressLint("RemoteViewLayout")
    fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews("com.example.myapplication", R.layout.notification)

        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.description, message)
        remoteView.setImageViewResource(R.id.app_logo, R.drawable.bell)

        return remoteView

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateNotification(title : String, message: String){

        val intent = Intent(this, MainActivity::class.java) // If clicked, then go to the app
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        // channel id and name

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelid)
            .setSmallIcon(R.drawable.bell)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title, message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelid, channelname, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, builder.build())

    }
}

private operator fun <E> MutableList<E>.compareTo(d: Double): Int {
return 0
}
