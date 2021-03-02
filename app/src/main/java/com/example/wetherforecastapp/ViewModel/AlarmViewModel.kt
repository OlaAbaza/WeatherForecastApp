package com.example.wetherforecastapp.ViewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wetherforecastapp.model.entity.Alarm
import com.example.wetherforecastapp.model.local.LocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmViewModel (application: Application) : AndroidViewModel(application) {

    lateinit var localDataSource : LocalDataSource
    val navegate = MutableLiveData<Alarm>()
    val displayListener = MutableLiveData<Alarm>()

    init{
        localDataSource = LocalDataSource(application)
    }

    public fun deleteAlarmObj(id: Int)  {

        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.deleteAlarmObj(id)
        }
    }

    public suspend fun insertAlarmObj(alarmObj: Alarm):Long  {

        return localDataSource.insertAlarm(alarmObj)

    }


    public fun onEditClick(alarmObj: Alarm)  {

        navegate.value=alarmObj

    }

    public fun getAlarmList() : LiveData<List<Alarm>> {
        return localDataSource.getAllAlarmObj()
    }


    fun getNavigate(): LiveData<Alarm> {
        return navegate
    }
}