package com.example.fleetmanagement.Database
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fleetmanagement.Image_Car
import kotlinx.coroutines.flow.Flow

@Dao
interface CarInterface {
  @Insert
  suspend fun insertCar(carEntity: Image_Car)
  @Update
  suspend fun updateCar(carEntity: Image_Car)
  @Delete
  suspend fun deleteCar(carEntity: Image_Car)
  @Query("SELECT * FROM FLEET_CARS")
  fun getAllCars(): Flow<List<Image_Car>>
  @Query("DELETE FROM Fleet_Cars")
  suspend fun deleteAllCars()
}