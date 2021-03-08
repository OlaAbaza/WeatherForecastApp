package com.example.wetherforecastapp.ui.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wetherforecastapp.R
import com.example.wetherforecastapp.databinding.DayItemBinding
import com.example.wetherforecastapp.model.entity.Daily
import java.util.*
import kotlin.collections.ArrayList

class DailyListAdapter(var DailyList: ArrayList<Daily>) :
    RecyclerView.Adapter<DailyListAdapter.DailyViewHolder>() {

    fun updateDaily(newDailyList: List<Daily>) {
        DailyList.clear()
        DailyList.addAll(newDailyList)
        notifyDataSetChanged()
    }
    class DailyViewHolder(var myView: DayItemBinding) : RecyclerView.ViewHolder(myView.root)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) : DailyViewHolder {
        val viewBinding = DayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyViewHolder(viewBinding)
    }

    override fun getItemCount() = DailyList.size
    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
       // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        val calendar_current: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(DailyList[position].dt.toLong()*1000)
        var date=calendar.get(Calendar.DAY_OF_MONTH).toString()+" "+calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
        holder.myView.minTemb.text = DailyList[position].temp.min.toInt().toString()+"°"
        holder.myView.maxTemb.text = DailyList[position].temp.max.toInt().toString()+"°/"
        holder.myView.dailyDay.text = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())+","+date
        //holder.myView.dayDesc.text = DailyList[position].weather.get(0).description.toString()


        if(calendar_current.get(Calendar.DAY_OF_MONTH)==calendar.get(Calendar.DAY_OF_MONTH)&&calendar_current.get(Calendar.MONTH)==calendar.get(Calendar.MONTH)){
            holder.myView.minTemb.setTextColor(Color.parseColor("#504FD3"))
            holder.myView.maxTemb.setTextColor(Color.parseColor("#504FD3"))
            holder.myView.dailyDay.setTextColor(Color.parseColor("#504FD3"))
            holder.myView.imgDay.setImageTintList(ColorStateList.valueOf(Color.parseColor("#504FD3")))
        }
        if(DailyList[position].weather.get(0).description.contains("clear" + "", ignoreCase = true)) {
            holder.myView.imgDay.setImageResource(R.drawable.ic_baseline_wb_sunny_24)
        }
        else {
            Glide.with(holder.myView.imgDay.context).load(iconLinkgetter(DailyList[position].weather.get(0).icon)).placeholder(R.drawable.ic_baseline_wb_sunny_24).into(holder.myView.imgDay)
        }

    }
    fun iconLinkgetter(iconName:String):String="https://openweathermap.org/img/wn/"+iconName+"@2x.png"
}