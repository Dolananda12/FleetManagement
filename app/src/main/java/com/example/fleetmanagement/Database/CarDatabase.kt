package com.example.fleetmanagement.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fleetmanagement.Image_Car

@Database(entities = [Image_Car::class], version = 1, exportSchema = false)
abstract class CarDatabase: RoomDatabase(){
    abstract val dao: CarInterface
    companion object{
        private var INSTANCE: CarDatabase?=null
        fun getInstance(context: Context):CarDatabase{
            synchronized(this){
                var instance= INSTANCE
                if(instance==null){
                    instance= Room.databaseBuilder(
                        context.applicationContext,
                        CarDatabase::class.java,
                        "Fleet_Cars"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE=instance
                }
                return  instance
            }
        }
    }
}