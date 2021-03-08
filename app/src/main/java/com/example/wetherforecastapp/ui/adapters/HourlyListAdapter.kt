package com.example.wetherforecastapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wetherforecastapp.R
import com.example.wetherforecastapp.databinding.HourItemBinding
import com.example.wetherforecastapp.model.entity.Hourly
import java.text.SimpleDateFormat
import java.util.*

class HourlyListAdapter(var HourlyList: ArrayList<Hourly>) : RecyclerView.Adapter<HourlyListAdapter.VH>() {

    fun updateHours(newHourlyList: List<Hourly>) {
        HourlyList.clear()
        HourlyList.addAll(newHourlyList)
        notifyDataSetChanged()
    }

    class VH(var myView: HourItemBinding) : RecyclerView.ViewHolder(myView.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewBinding =
                HourItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(viewBinding)
    }

    private fun timeFormat(millisSeconds: Int): String {
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis((millisSeconds * 1000).toLong())
        val format = SimpleDateFormat("hh:00 aaa")
        return format.format(calendar.time)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.myView.Hour.text = timeFormat(HourlyList[position].dt)
        holder.myView.hourlyTemb.text = HourlyList[position].temp.toInt().toString() + "°"
        if(HourlyList[position].weather.get(0).description.contains("01d" + "", ignoreCase = true)) {
            holder.myView.hourlyImg.setImageResource(R.drawable.ic_baseline_wb_sunny_24)
        }
        else{ Glide.with(holder.myView.hourlyImg.context).load(iconLinkgetter(HourlyList[position].weather.get(0).icon)).placeholder(R.drawable.ic_baseline_wb_sunny_24).into(holder.myView.hourlyImg)}

    }

    override fun getItemCount() = HourlyList.size

    fun iconLinkgetter(iconName: String): String = "https://openweathermap.org/img/wn/" + iconName + "@2x.png"

}