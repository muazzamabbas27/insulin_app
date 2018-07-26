package com.ciklum.insulinapp.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ciklum.insulinapp.Models.BasalBGRecyclerView
import com.ciklum.insulinapp.Models.BolusBGRecyclerView
import com.ciklum.insulinapp.R

class adapter2(private val data: ArrayList<BasalBGRecyclerView>) : RecyclerView.Adapter<adapter2.adapter2ViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): adapter2ViewHolder {

        val inflater = LayoutInflater.from(viewGroup.context)
        var view: View =inflater.inflate(R.layout.list_item_bg_basal, viewGroup, false)
        return adapter2ViewHolder(view)
    }

    override fun onBindViewHolder(adapter2ViewHolder: adapter2ViewHolder, i: Int) {

        for (j in 0 until data.size) {
            adapter2ViewHolder.weightTextView.setText(data[i].mWeight)
            adapter2ViewHolder.TDITextView.setText(data[i].mTDI)
            adapter2ViewHolder.insulinRecommendationTextView.setText(data[i].insulinRecommended)
            adapter2ViewHolder.insulinTypeTextView.setText(data[i].insulinType)
        }
    }


    override fun getItemCount(): Int {
        return data.size
    }

    inner class adapter2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var weightTextView:TextView
        var TDITextView:TextView
        var insulinRecommendationTextView:TextView
        var insulinTypeTextView:TextView

        init {
            weightTextView=itemView.findViewById(R.id.weightTextView)
            TDITextView=itemView.findViewById(R.id.TDITextView)
            insulinRecommendationTextView=itemView.findViewById(R.id.insulinRecommendationTextView)
            insulinTypeTextView=itemView.findViewById(R.id.insulinTypeTextView)
        }
    }

}