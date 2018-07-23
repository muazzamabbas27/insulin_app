package com.ciklum.insulinapp.Models

class BolusBGLevel constructor(emailID:String, typeOfBG:String, beforeEvent:String, currentBGLevel: Int, targetBGLevel:Int, totalCHO:Int, amountDisposedByInsulin:Int, correctionFactor:Int, insulinRecommendation:Int, calendarTime:String)
{
    var emailID:String=emailID
    var typeOfBG:String=typeOfBG
    var beforeEvent:String=beforeEvent
    var currentBGLevel:Int=currentBGLevel
    var targetBGLevel:Int=targetBGLevel
    var totalCHO:Int=totalCHO
    var amountDisposedByInsulin:Int=amountDisposedByInsulin
    var correctionFactor:Int=correctionFactor
    var insulinRecommendation:Int=insulinRecommendation
    var calendarTime:String=calendarTime
}