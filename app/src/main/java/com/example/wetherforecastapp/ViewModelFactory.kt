package com.example.wetherforecastapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wetherforecastapp.model.remote.WetherApi






class ViewModelFactory(private val apiHelper: WetherApi) {
    //: ViewModelProvider.Factory {

    /*override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(apiHelper) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }*/

}