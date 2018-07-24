package com.ciklum.insulinapp.Adapters


import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import com.ciklum.insulinapp.Models.BGRecyclerView
import com.ciklum.insulinapp.R
import org.w3c.dom.Text


class adapter1(private val data: ArrayList<BGRecyclerView>) : RecyclerView.Adapter<adapter1.adapter1ViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): adapter1ViewHolder {

        val inflater = LayoutInflater.from(viewGroup.context)
        var view:View=inflater.inflate(R.layout.list_item_bg_bolus, viewGroup, false)
        return adapter1ViewHolder(view)
    }

    override fun onBindViewHolder(adapter1ViewHolder: adapter1ViewHolder, i: Int) {

        for (j in 0 until data.size) {
            adapter1ViewHolder.eventTextView.setText(data[i].beforeEvent)
            adapter1ViewHolder.initialBGTextView.setText(data[i].initialBG)
            adapter1ViewHolder.targetBGTextView.setText(data[i].targetBG)
            adapter1ViewHolder.amountOfCHOTextView.setText(data[i].amountOfCHO)
            adapter1ViewHolder.disposedCHOTextView.setText(data[i].disposedCHO)
            adapter1ViewHolder.correctionFactorTextView.setText(data[i].correctionFactor)
            adapter1ViewHolder.insulinRecommendedTextView.setText(data[i].insulinRecommendation)
            adapter1ViewHolder.insulinTypeTextView.setText(data[i].insulinType)
        }
    }


    override fun getItemCount(): Int {
        return data.size
    }

    inner class adapter1ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var eventTextView: TextView
        var initialBGTextView:TextView
        var targetBGTextView:TextView
        var amountOfCHOTextView:TextView
        var disposedCHOTextView:TextView
        var correctionFactorTextView:TextView
        var insulinRecommendedTextView:TextView
        var insulinTypeTextView:TextView

        init {
            eventTextView=itemView.findViewById(R.id.eventTextView)
            initialBGTextView=itemView.findViewById(R.id.initialBGTextView)
            targetBGTextView=itemView.findViewById(R.id.targetBGTextView)
            amountOfCHOTextView=itemView.findViewById(R.id.amountOfCHOTextView)
            disposedCHOTextView=itemView.findViewById(R.id.disposedCHOTextView)
            correctionFactorTextView=itemView.findViewById(R.id.correctionFactorTextView)
            insulinRecommendedTextView=itemView.findViewById(R.id.insulinRecommendedTextView)
            insulinTypeTextView=itemView.findViewById(R.id.insulinTypeTextView)
        }
    }

}