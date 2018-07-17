package com.ciklum.insulinapp.Models

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver:BroadcastReceiver()
{
    override fun onReceive(p0: Context?, p1: Intent?) {

        var getResult:String= p1!!.getStringExtra("extra")

        var serviceIntent:Intent=Intent(p0,RingtoneService::class.java)
        serviceIntent.putExtra("extra",getResult)
        p0?.startService(serviceIntent)
    }

}