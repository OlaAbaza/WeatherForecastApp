package com.example.wetherforecastapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wetherforecastapp.ViewModels.AlarmViewModel
import com.example.wetherforecastapp.databinding.AlarmItemBinding
import com.example.wetherforecastapp.model.entity.Alarm
import java.util.ArrayList

class AlarmAdapter (var alarmList: ArrayList<Alarm>, alartViewModel: AlarmViewModel, context: Context) : RecyclerView.Adapter<AlarmAdapter.VH>() {
     var context: Context
     var alartViewModel: AlarmViewModel
    init {
        this.context=context
        this.alartViewModel=alartViewModel
    }


    fun updateAlarms(newAlarmList: List<Alarm>) {
        alarmList.clear()
        alarmList.addAll(newAlarmList)
        notifyDataSetChanged()
    }

    class VH(var myView: AlarmItemBinding) : RecyclerView.ViewHolder(myView.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewBinding =
                AlarmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(viewBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.myView.alarmType.text =alarmList[position].event
        holder.myView.dateTime.text = alarmList[position].Date+" "+alarmList[position].start+" to "+alarmList[position].end
        holder.myView.details.text =alarmList[position].description

        holder.myView.editBtn.setOnClickListener {
            alartViewModel.onEditClick(alarmList[position])
        }

    }
    fun getItemAt(pos:Int)=alarmList.get(pos)
    override fun getItemCount() = alarmList.size

}