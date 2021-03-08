package com.example.wetherforecastapp.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wetherforecastapp.model.entity.Alarm
import com.example.wetherforecastapp.model.local.LocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    var localDataSource: LocalDataSource
    val navegate = MutableLiveData<Alarm>()


    init {
        localDataSource = LocalDataSource(application)
    }

    fun deleteAlarmObj(id: Int) {

        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.deleteAlarmObj(id)
        }
    }

    suspend fun insertAlarmObj(alarmObj: Alarm): Long {

        return localDataSource.insertAlarm(alarmObj)

    }


    fun onEditClick(alarmObj: Alarm) {

        navegate.value = alarmObj

    }

    fun getAlarmList(): LiveData<List<Alarm>> {
        return localDataSource.getAllAlarmObj()
    }


    fun getNavigate(): LiveData<Alarm> {
        return navegate
    }
}