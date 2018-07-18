package com.ciklum.insulinapp.Adapters


import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import com.ciklum.insulinapp.Models.BGRecyclerView
import com.ciklum.insulinapp.R


class adapter1(private val data: ArrayList<BGRecyclerView>) : RecyclerView.Adapter<adapter1.adapter1ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): adapter1ViewHolder {

        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.list_item_bg, viewGroup, false)
        return adapter1ViewHolder(view)
    }

    override fun onBindViewHolder(adapter1ViewHolder: adapter1ViewHolder, i: Int) {

        for (j in 0 until data.size) {
            adapter1ViewHolder.eventTextView.setText(data[i].recentEvent)
            adapter1ViewHolder.foodTextView.setText(data[i].foodIntake)
            adapter1ViewHolder.bgTextView.setText(data[i].BGLevel)
            adapter1ViewHolder.insulinTextView.setText(data[i].insulinLevel)
        }
    }


    override fun getItemCount(): Int {
        return data.size
    }

    inner class adapter1ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var eventTextView: TextView
        var foodTextView:TextView
        var bgTextView: TextView
        var insulinTextView: TextView

        init {
            eventTextView=itemView.findViewById(R.id.eventTextView)
            foodTextView=itemView.findViewById(R.id.foodTextView)
            bgTextView=itemView.findViewById(R.id.bgTextView)
            insulinTextView=itemView.findViewById(R.id.insulinTextView)
        }
    }

}