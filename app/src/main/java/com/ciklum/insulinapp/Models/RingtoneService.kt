package com.ciklum.insulinapp.Models

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.ciklum.insulinapp.Activities.MedicineReminder.ReminderActivity
import com.ciklum.insulinapp.R

class RingtoneService:Service(){
    companion object {
        lateinit var r:Ringtone
    }

    var id:Int=0
    var isRunning:Boolean=false

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var state:String= intent!!.getStringExtra("extra")

        assert(state!=null)

        when(state)
        {
            "on"->id=1
            "off"->id=0
        }

        if(!this.isRunning && id==1)
        {
            playAlarm()
            this.isRunning=true
            this.id=0
            fireNotification()
        }

        else if(this.isRunning && id==0)
        {
            r.stop()
            this.isRunning=false
            this.id=0
        }

        else if(!this.isRunning && id==0)
        {
            this.isRunning=false
            this.id=0
        }

        else if(this.isRunning && id==1)
        {
            this.isRunning=true
            this.id=1
        }

        else
        {

        }
        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fireNotification() {
        var anotherIntent:Intent=Intent(this,ReminderActivity::class.java)
        var p1:PendingIntent= PendingIntent.getActivity(this,0,anotherIntent,0)
        val defaultSoundUri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        var notifyManager: NotificationManager =getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var notification:Notification=Notification.Builder(this)
                .setContentTitle("Medicine Time")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(defaultSoundUri)
                .setContentText("Time to take your medicine")
                .setContentIntent(p1)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build()


        notifyManager.notify(0,notification)
    }

    private fun playAlarm() {
        var alarmUri: Uri =RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        if(alarmUri==null)
        {
            alarmUri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        r=RingtoneManager.getRingtone(baseContext,alarmUri)
        r.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.isRunning=false
    }
}