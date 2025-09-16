package com.example.fleetmanagement.Database

import com.example.fleetmanagement.Image_Car

class Car_Repository(private val dao: CarInterface) {
    val cars = dao.getAllCars()
    suspend fun insertCar(imageCar: Image_Car){
        dao.insertCar(imageCar)
        println("inserted Successfully")
    }
    suspend fun upDateCar(carEntity: Image_Car){
        dao.updateCar(carEntity)
    }
    suspend fun deleteCar(carEntity: Image_Car){
        dao.deleteCar(carEntity)
    }
    suspend fun deleteAllCars(){
        dao.deleteAllCars()
    }
}