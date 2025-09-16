package com.example.fleetmanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fleetmanagement.Database.Car_Repository
import java.lang.IllegalArgumentException

class ViewModelFactory(private val carRepository: Car_Repository) :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainActivityViewModel::class.java)){
            return MainActivityViewModel(carRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}