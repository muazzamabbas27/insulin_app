package com.ciklum.insulinapp.Activities.LogBG

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import com.ciklum.insulinapp.R


class LogBGActivity : AppCompatActivity() {


    private lateinit var currentBGEditText: EditText
    private lateinit var targetBGEditText: EditText
    private lateinit var CHOEditText:EditText
    private lateinit var CHODisposedEditText: EditText
    private lateinit var correctionFactorEditText: EditText
    private lateinit var showInsulinTextView:TextView
    private lateinit var checkInsulinBtn:Button

    private lateinit var currentBG:String
    private lateinit var targetBG:String
    private lateinit var CHO:String
    private lateinit var CHODisposed:String
    private lateinit var correctionFactor:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_bg)

        currentBGEditText=findViewById(R.id.currentBGEditText)
        targetBGEditText=findViewById(R.id.targetBGEditText)
        CHOEditText=findViewById(R.id.CHOEditText)
        CHODisposedEditText=findViewById(R.id.CHODisposedEditText)
        correctionFactorEditText=findViewById(R.id.correctionFactorEditText)
        showInsulinTextView=findViewById(R.id.showInsulinTextView)
        checkInsulinBtn=findViewById(R.id.checkInsulinBtn)

        checkInsulinBtn.setOnClickListener()
        {
            currentBG=currentBGEditText.text.toString().trim()
            if(TextUtils.isEmpty(currentBG))
            {
                Toast.makeText(applicationContext,"You haven't entered your current blood glucose level",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var currentBGNum:Int=currentBG.toInt()

            targetBG=targetBGEditText.text.toString().trim()
            if(TextUtils.isEmpty(targetBG))
            {
                Toast.makeText(applicationContext,"You haven't entered your target blood glucose level",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var targetBGNum:Int=targetBG.toInt()


            if(currentBGNum<targetBGNum)
            {
                Toast.makeText(applicationContext,"You don't need to administer Insulin",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CHO=CHOEditText.text.toString().trim()
            if(TextUtils.isEmpty(CHO))
            {
                Toast.makeText(applicationContext,"You haven't entered the CHO amount in your food",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var CHONum:Int=CHO.toInt()

            CHODisposed=CHODisposedEditText.text.toString().trim()
            if(TextUtils.isEmpty(CHODisposed))
            {
                Toast.makeText(applicationContext,"You haven't entered the amount of CHO disposed by 1 unit of Insulin",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var CHODisposedNum:Int=CHODisposed.toInt()

            correctionFactor=correctionFactorEditText.text.toString().trim()
            if(TextUtils.isEmpty(correctionFactor))
            {
                Toast.makeText(applicationContext,"You haven't entered the correction factor",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var correctionFactorNum=correctionFactor.toInt()

            //Insulin Recommendation Calculation

            var differenceBGLevel:Int=currentBGNum-targetBGNum
            var insulinDose:Int=CHONum/CHODisposedNum
            var highBloodSugarCorrectionDose:Int=differenceBGLevel/correctionFactorNum
            var totalInsulinRecommendation:Int=insulinDose+highBloodSugarCorrectionDose

            var totalInsulinRecommendationString:String=totalInsulinRecommendation.toString().trim()

            showInsulinTextView.setText("You should take " + totalInsulinRecommendationString + " units of Insulin")


        }
    }
}
