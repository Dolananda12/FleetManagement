package com.example.fleetmanagement

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import androidx.lifecycle.viewModelScope
import com.example.fleetmanagement.Database.CarEntity
import com.example.fleetmanagement.Database.Car_Repository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class MainActivityViewModel(private val carRepository: Car_Repository) : ViewModel() {
    fun getCars() : Flow<List<Image_Car>> = carRepository.cars
    var carEntity : Image_Car? = null
    var userName : String = ""
    var carEntity_1 : CarEntity? = null
    var car_name_2 : String =""
    var selected_Car : Image_Car? = null
    var user: User? = null
    fun deleteAllCars(){
        viewModelScope.launch {
            carRepository.deleteAllCars()
        }
    }
    fun injectData(){
        viewModelScope.launch {
            for(i in 0..<carList.size){
              insertCar(carList[i])
            }
        }
    }
    fun authenticate(userName: String, password: String): Flow<Image_Car?> = callbackFlow {
        Log.i("MYTAG", "Authenticating: $userName and $password")

        val db = Firebase.firestore
        val query = db.collection("fleet_users")

        val listener = query.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.i("MYTAG", "${document.getString("username")} and ${document.getString("password")}")
                    if (document.getString("username") == userName && document.getString("password") == password) {
                        Log.i("MYTAG", "User authenticated successfully")
                        println("car_name: ${document.getString("car_name")}")
                        val carEntity = document.getString("car_name")
                        viewModelScope.launch(Dispatchers.IO) {
                            val carEntity = changeCarEntity(document.getString("car_name").orEmpty()).await()
                            Log.i("MYTAG", carEntity.toString())
                            val car_name = document.getString("car_name")
                            val company_name = document.getString("company_name")
                            val imagePath = document.getString("imagePath")
                            user = User(userName, password,car_name!!,company_name!!,imagePath!!.toInt())
                            send(Image_Car(imagePath.toInt(),car_name,company_name)) // Safe send inside callbackFlow
                            close() // Close the flow after sending the result
                        }
                        return@addOnSuccessListener
                    }
                }
                trySend(null) // Send null if no user found
                close() // Close the flow safely
            }
            .addOnFailureListener { e ->
                Log.e("MYTAG", "Error fetching users: ${e.message}")
                trySend(null) // Send null on failure
                close()
            }

        awaitClose { }
    }

    fun changeCarEntity(car_name : String) : CompletableDeferred<Image_Car?>{
        val h = CompletableDeferred<Image_Car?>(null)
        viewModelScope.launch {
            carRepository.cars.collectLatest {
                val list = it
                for(i in list.indices){
                    if(list[i].car_name==car_name){
                        carEntity=list[i]
                        println("found: "+carEntity.toString())
                        update_4(carEntity!!)
                        h.complete(carEntity)
                        break
                    }
                }
            }
        }
        return h
    }
    fun newUserRegistration(user: User) :CompletableDeferred<Boolean?>{
        val db= Firebase.firestore
        val user1 = hashMapOf(
                "username" to user.userName,
                "password" to user.password,
                "car_name" to user.car_name,
                "company_name" to user.company_name,
                "imagePath" to user.imagePath.toString()
        )
        val h = CompletableDeferred<Boolean?>(null)
        Log.i("MYTAG", "Authenticating: $user1")
        viewModelScope.launch {
            db.collection("fleet_users")
                .add(user1)
                .addOnSuccessListener {
                    changeCarEntity(user.car_name)
                    userName=user.userName
                    OnSuccessfulCompletion(user)
                    h.complete(true)
                }.addOnFailureListener { exception->
                    Log.w(TAG,exception)
                    h.complete(false)
                }
        }
        return h
    }
    fun OnSuccessfulCompletion(user: User) {
        val database = FirebaseDatabase.getInstance()
        val userName = user.userName
        if (userName.isNullOrEmpty()) {
            Log.e("FirebaseDB", "Username is null or empty!")
            return
        }
        val fleetDataRef = database.getReference("fleet_data").child(userName)
        val newCarData = mapOf(
            "estimated_mileage" to "20 km/l",
            "battery_level" to 100,
            "engine_health" to "Good",
            "latitude" to 0.0,
            "longitude" to 0.0,
            "speed" to 0.0,
            "warning" to "none",
            "timestamp" to "2025-04-29 17:09:29"
        )
        fleetDataRef.setValue(newCarData)
            .addOnSuccessListener {
                Log.d("FirebaseDB", "New car entry added successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseDB", "Failed to add car entry", e)
            }
    }

    fun sendDataToAddUi(mode : Boolean): Flow<List<Image_Car>> = channelFlow {
        val list_1: MutableList<Image_Car> = mutableListOf()
        val list_2: MutableList<Image_Car> = mutableListOf()
        launch {
            carRepository.cars.collectLatest { carList ->
                list_1.clear()
                list_2.clear()
                for (car in carList) {
                    list_1.add(car)
                    list_2.add(car)
                }
                println("Emitting: $list_1")
                if(mode){
                    send(list_1)
                }else{
                    send(list_2)
                }
            }
        }
    }
    fun update_2(carEntity: Image_Car){
        viewModelScope.launch {
            carRepository.upDateCar(carEntity)
        }
    }
    fun update_4(carEntity: Image_Car){
        viewModelScope.launch {
            carRepository.upDateCar(carEntity)
        }
    }

    fun getCarEntityFlow(): Flow<CarEntity?> = callbackFlow {
        val userName = user!!.userName
        val database = FirebaseDatabase.getInstance().reference
        val userRef = database.child("fleet_data_1").child(userName)
        Log.i("MYTAG","Querying realtime database for $userName")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val carEntity = CarEntity(
                        id = 0,
                        est_milage = snapshot.child("estimated_mileage").getValue(String::class.java),
                        batteryLevel = snapshot.child("battery_level").getValue(Double::class.java),
                        engineHealth = snapshot.child("engine_health").getValue(String::class.java),
                        latitude = snapshot.child("latitude").getValue(Double::class.java),
                        longitude = snapshot.child("longitude").getValue(Double::class.java),
                        speed = snapshot.child("speed").getValue(Double::class.java),
                        warning = snapshot.child("warning").getValue(String::class.java),
                        timestamp = snapshot.child("timestamp").getValue(String::class.java)
                    )
                    trySend(carEntity)
                } else {
                    trySend(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        userRef.addValueEventListener(listener)

        awaitClose { userRef.removeEventListener(listener) }
    }.flowOn(Dispatchers.IO)
    fun update_3(carEntity: Image_Car) : CompletableDeferred<Boolean?>{
        val s = CompletableDeferred<Boolean?>(null)
        viewModelScope.launch {
            carRepository.upDateCar(carEntity)
        }.invokeOnCompletion {
            s.complete(true)
        }
        return s
    }

    fun insertCar(carEntity: Image_Car){
        viewModelScope.launch {
            carRepository.insertCar(carEntity)
        }
    }
}