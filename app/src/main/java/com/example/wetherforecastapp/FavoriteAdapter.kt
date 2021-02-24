package com.example.wetherforecastapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wetherforecastapp.databinding.FavItemBinding
import com.example.wetherforecastapp.model.entity.WeatherData
import java.util.*

class FavoriteAdapter(var FavList: ArrayList<WeatherData>,favoritesViewModel: FavViewModel,context: Context) : RecyclerView.Adapter<FavoriteAdapter.VH>() {

     var favoritesViewModel: FavViewModel
     var context: Context
    init {
        this.favoritesViewModel=favoritesViewModel
        this.context=context
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
        holder.myView.favDesc.text =FavList[position].currentWether.weather.get(0).description.toString()
        holder.myView.timezoneTxt.text = FavList[position].timezone
        holder.myView.favTemb.text = FavList[position].currentWether.temp.toInt().toString()+"°"

    }

    override fun getItemCount() = FavList.size

}