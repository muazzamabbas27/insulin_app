package com.ciklum.insulinapp.Models

class BasalBGLevel constructor(emailID:String,typeOfBG:String,mWeight:Int,mTDI:Double,insulinRecommendation:Double, calendarTime:String)
{
    var emailID:String=emailID
    var typeOfBG:String=typeOfBG
    var mWeight:Int=mWeight
    var mTDI:Double=mTDI
    var insulinRecommendation:Double=insulinRecommendation
    var calendarTime=calendarTime
}