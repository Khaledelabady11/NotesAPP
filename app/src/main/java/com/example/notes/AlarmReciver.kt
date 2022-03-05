package com.example.notes

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReciver:BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val i=Intent(p0,MainActivity::class.java)
        i.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent=PendingIntent.getActivity(p0,0,i,0)
        val builder=NotificationCompat.Builder(p0!!,"foxAndroid")
            .setSmallIcon(R.drawable.noteicon)
            .setContentTitle("Note Alarm Manager")
            .setContentText("See your Notes Now..")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notficationManager=NotificationManagerCompat.from(p0)
        notficationManager.notify(123,builder.build())




    }
}