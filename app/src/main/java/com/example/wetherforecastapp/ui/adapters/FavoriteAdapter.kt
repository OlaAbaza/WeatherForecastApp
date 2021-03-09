package com.example.wetherforecastapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wetherforecastapp.R
import com.example.wetherforecastapp.ViewModels.FavViewModel
import com.example.wetherforecastapp.databinding.FavItemBinding
import com.example.wetherforecastapp.model.entity.WeatherData
import java.util.*

class FavoriteAdapter(
    var FavList: ArrayList<WeatherData>,
    favoritesViewModel: FavViewModel,
    context: Context,
    timeZone: String
) : RecyclerView.Adapter<FavoriteAdapter.VH>() {

    var favoritesViewModel: FavViewModel
    var context: Context
    var timezone: String = timeZone

    init {
        this.favoritesViewModel = favoritesViewModel
        this.context = context
    }

    fun updateHours(newHourlyList: List<WeatherData>) {
        FavList.clear()
        FavList.addAll(newHourlyList)
        notifyDataSetChanged()
    }

    class VH(var myView: FavItemBinding) : RecyclerView.ViewHolder(myView.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewBinding =
            FavItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(viewBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        var split_timezone = FavList[position].timezone.split("/")
        holder.myView.favDesc.text = FavList[position].currentWether.weather.get(0).description
        holder.myView.timezoneTxt.text = split_timezone[0] + "\n" + split_timezone[1]
        holder.myView.favTemb.text = FavList[position].currentWether.temp.toInt().toString() + "°"
        if (FavList[position].timezone == timezone)
            holder.myView.currentLoc.visibility = View.VISIBLE

        when {
            FavList[position].currentWether.weather.get(0).icon.contains(
                "02d" + "",
                ignoreCase = true
            ) -> holder.myView.decIcon.setImageResource(R.drawable.cloud_sun)
            FavList[position].currentWether.weather.get(0).icon.contains(
                "09" + "",
                ignoreCase = true
            ) || FavList[position].currentWether.weather.get(0).icon.contains(
                "10" + "",
                ignoreCase = true
            ) -> holder.myView.decIcon.setImageResource(R.drawable.rain)
            FavList[position].currentWether.weather.get(0).icon.contains(
                "13" + "",
                ignoreCase = true
            ) -> holder.myView.decIcon.setImageResource(R.drawable.snow)
            FavList[position].currentWether.weather.get(0).icon.contains(
                "02n" + "",
                ignoreCase = true
            ) -> holder.myView.decIcon.setImageResource(R.drawable.cloud_night)
            FavList[position].currentWether.weather.get(0).icon.contains(
                "01d" + "",
                ignoreCase = true
            ) -> holder.myView.decIcon.setImageResource(R.drawable.sun)
            FavList[position].currentWether.weather.get(0).icon.contains(
                "01n" + "",
                ignoreCase = true
            ) -> holder.myView.decIcon.setImageResource(R.drawable.night_icon)
            FavList[position].currentWether.weather.get(0).icon.contains(
                "11" + "",
                ignoreCase = true
            ) -> holder.myView.decIcon.setImageResource(R.drawable.ic_wind)
            else -> holder.myView.decIcon.setImageResource(R.drawable.ic_cloud1)
        }

        holder.myView.favItem.setOnClickListener {
            favoritesViewModel.onShowClick(FavList[position])
        }
    }

    fun getItemAt(pos: Int) = FavList.get(pos)

    override fun getItemCount() = FavList.size

}