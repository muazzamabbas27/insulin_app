package com.ciklum.insulinapp.Models

import android.app.AlarmManager
import android.app.PendingIntent

class AlarmDB constructor(emailID:String,mTime:String,myPendingIntent: PendingIntent,myAlarmManager: AlarmManager)
{
    var emailID:String=emailID
    var mTime:String=mTime
    var myPendingIntent:PendingIntent=myPendingIntent
    var myAlarmManager:AlarmManager=myAlarmManager
}