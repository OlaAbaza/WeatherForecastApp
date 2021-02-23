package com.example.wetherforecastapp.View

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wetherforecastapp.R
import com.example.wetherforecastapp.model.entity.Daily



class WeatherDailyListAdapter(var DailyList: ArrayList<Daily>) :
    RecyclerView.Adapter<WeatherDailyListAdapter.DailyViewHolder>() {

    fun updateCountries(newDailyList: List<Daily>) {
        DailyList.clear()
        DailyList.addAll(newDailyList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = DailyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.day_item, parent, false)
    )

    override fun getItemCount() = DailyList.size

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        //holder.bind(DailyList[position])
    }

    class DailyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
      /*  private val countryName = view.name
        private val countryCapital = view.capital

        fun bind(country: Country) {
            countryName.text = country.name
            countryCapital.text = country.capital

        }*/
    }
}