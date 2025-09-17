package com.example.fleetmanagement

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fleetmanagement.Database.CarDatabase
import com.example.fleetmanagement.Database.CarEntity
import com.example.fleetmanagement.Database.Car_Repository
import com.example.fleetmanagement.OnBoardingScreens.OnboardingScreen
import com.example.fleetmanagement.ui.theme.FleetManagementTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private  lateinit var viewModel: MainActivityViewModel
    var car_name_1 =""
    var userName =""
    var passWord =""
    var screen_mode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val dao = CarDatabase.getInstance(application).dao
        val car_repository = Car_Repository(dao)
        val factory = ViewModelFactory(car_repository)
        viewModel = ViewModelProvider(this,factory)[MainActivityViewModel::class.java]
        viewModel.changeCarEntity("")
        CoroutineScope(Dispatchers.IO).launch{
            viewModel.getCars().collectLatest { it->
                if(it.isEmpty()){
                    viewModel.injectData()
                }
            }
        }
        setContent {
            FleetManagementTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .background(Color.White)
                ) { innerPadding ->
                    MainScreen(paddingValues = innerPadding)
                }
            }
        }
    }
    val LightBlue = Color(0xFF0F9AD3)
    @Composable
    fun MainScreen(paddingValues: PaddingValues) {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "onboarding"
        ) {
            composable("login_screen"){ LoginPage(navHostController = navController)}
            composable("onboarding") { OnboardingScreen(paddingValues = paddingValues,navHostController = navController) }
            composable("home") { Dashboard(navController) }
            composable("car_info"){
                BlankPage()
            }
            composable("newUser"){
                NewUserOnBoardingScreen(navHostController = navController)
            }
            composable("screen0"){
                Screen0(navController)
            }
            composable("userPage"){
                UserPage(navHostController = navController)
            }
        }
    }
    @Composable
    fun HandleBackPress(onClick: () -> Unit){
        var confirm by remember {
            mutableStateOf(false)
        }
        AlertDialog(
            onDismissRequest = { onClick() },
            title = { Text("Exit App") },
            text = { Text("Are you sure you want to exit the application?") },
            confirmButton = {
                TextButton(onClick = {
                    confirm = true
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onClick()
                }) {
                    Text("No")
                }
            }
        )
        if (confirm) {
            (LocalContext.current as? Activity)?.finish()
        }
    }
    @Composable
    fun HandleLogout(onClick: () -> Unit,navHostController: NavHostController){
        AlertDialog(
            onDismissRequest = { onClick() },
            title = { Text("") },
            text = { Text("Logout?") },
            confirmButton = {
                TextButton(onClick = {
                    navHostController.navigate("login_screen")
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { onClick()}) {
                    Text("No")
                }
            }
        )
    }
    @Composable
    fun UserPage(navHostController: NavHostController) {
        val userName = viewModel.user!!.userName
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var showDialog_1 by remember {
                mutableStateOf(false)
            }
            var car_Entity by remember {
                mutableStateOf<CarEntity?>(null)
            }
            LaunchedEffect(true) {
                val database = FirebaseDatabase.getInstance().reference
                val userRef = database.child("fleet_data_1").child(userName!!)
                userRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val car = CarEntity(
                                id = 0,
                                accel_x = snapshot.child("accel_x").getValue(Double::class.java),
                                accel_y = snapshot.child("accel_y").getValue(Double::class.java),
                                accel_z = snapshot.child("accel_z").getValue(Double::class.java),
                                latitude = snapshot.child("latitude").getValue(Double::class.java),
                                longitude = snapshot.child("longitude").getValue(Double::class.java),
                                datetime = snapshot.child("datetime").getValue(String::class.java),
                                pitch = snapshot.child("pitch").getValue(Double::class.java),
                                roll = snapshot.child("roll").getValue(Double::class.java),
                                timestamp = snapshot.child("timestamp").getValue(Double::class.java),
                                yaw = snapshot.child("yaw").getValue(Double::class.java)
                            )
                            car_Entity = car
                        } else {
                            Log.e("Firebase", "No data found for user $userName")
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase", "Failed to read data: ${error.message}")
                    }
                })
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightBlue), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "User Dashboard",
                    textAlign = TextAlign.Left,
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp), horizontalArrangement = Arrangement.End
                ) {
                    Icon(Icons.Filled.ExitToApp,
                        contentDescription = "null",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                showDialog_1 = true
                            },
                        tint = Color.White
                    )
                }
            }
            var s by remember {  mutableStateOf(false) }
            BackHandler {
                Log.i("MYTAG","back pressed!")
                s=true
            }
            if(s){
                HandleBackPress({
                    s=false
                })
            }
            if (showDialog_1) {
                //login screen
                HandleLogout({
                    showDialog_1=false
                },navHostController)
            }
            if(car_Entity!=null) {
                Card(modifier = Modifier.padding(7.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Welcome\n\n ${userName}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = LightBlue,
                            modifier = Modifier
                                .widthIn(0.dp, LocalConfiguration.current.screenWidthDp * (0.4).dp)
                                .padding(5.dp)
                        )
                        Image(
                            painter = painterResource(id = viewModel.user!!.imagePath),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .padding(5.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp), horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Car Information",
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                BlankPage()
            }else{
                val progress by remember { mutableFloatStateOf(0f) }
                Column(modifier = Modifier.fillMaxSize(),verticalArrangement=Arrangement.Center,horizontalAlignment=Alignment.CenterHorizontally){
                    IndeterminateCircularIndicator()
                }
            }
        }
    }
    @Composable
    fun TopBar1(){
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .statusBarsPadding()
            .background(LightBlue)){
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Fleet Management", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
    @Composable
    fun Screen0(navHostController: NavHostController){
        val activity = LocalContext.current as? Activity
        Scaffold(topBar = {
              TopBar1()
        }){innerPadding->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(100.dp))
                Text("Login Options", fontWeight = FontWeight.Bold, color = LightBlue, fontSize = 27.sp)
                Spacer(Modifier.height(20.dp))
                Button({screen_mode= false
                    navHostController.navigate("login_screen")}) {
                    Text("Admin Login Screen", fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Button({screen_mode= true
                    navHostController.navigate("login_screen")}) {
                      Text("User Login Screen", fontWeight = FontWeight.Bold, color = LightBlue)
                }
            }
            BackHandler(enabled = true) {
                    activity?.finish()
            }
        }
    }
    @Composable
    fun LoginPage(navHostController: NavHostController){
       BackHandler {
           navHostController.navigate("screen0")
       }
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
          Image(painter = painterResource(id = R.drawable.onboarding_1), contentDescription = "", modifier = Modifier.size(200.dp))
          Text(text = "Welcome to the Fleet Management Application", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = LightBlue, textAlign = TextAlign.Center)
      }
      Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
          var userName by remember {
              mutableStateOf("")
          }
          var passWord by remember {
              mutableStateOf("")
          }
          OutlinedTextField(value =userName, onValueChange = {it->
              userName=it
          }, label = {
              Text("Username")
          })
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(value =passWord, onValueChange = {it->
              passWord=it
          }, label = {
              Text("Password")
          })
          Spacer(modifier = Modifier.height(20.dp))
          Button(onClick = {
              CoroutineScope(Dispatchers.IO).launch {
                  var a : Image_Car? = null
                  async {
                      this@MainActivity.userName=userName
                      this@MainActivity.passWord=passWord
                      viewModel.authenticate(userName,passWord).collectLatest {
                          a=it
                          Log.i("MYTAG",it.toString())
                      }
                  }.await()
                  var h =""
                  if(userName=="saikiran.k22@iiits.in"&&passWord=="123456"){
                      h="Admin"
                  }else if(a!=null){
                      h="User"
                  }else{
                      h="Incorrect Credentials Entered"
                  }
                  withContext(Dispatchers.Main){
                      Toast.makeText(applicationContext,h, Toast.LENGTH_SHORT).show()
                      if(h=="Admin"){
                          navHostController.navigate("home")
                      }
                      if(h=="User"){
                          navHostController.navigate("userPage")
                      }
                  }
              }
          }, colors = ButtonColors(containerColor = LightBlue, contentColor = Color.White, disabledContentColor = Color.White, disabledContainerColor = LightBlue)) {
              Text(text = "Login", fontWeight = FontWeight.Bold, fontSize = 24.sp)
          }
          Spacer(modifier = Modifier.height(15.dp))
          if(screen_mode) {
              Button(
                  onClick = {
                      navHostController.navigate("newUser")
                  },
                  shape = RectangleShape,
                  colors = ButtonColors(
                      containerColor = Color.White,
                      contentColor = Color.Black,
                      disabledContentColor = Color.Black,
                      disabledContainerColor = Color.White
                  ),
                  modifier = Modifier.border(1.dp, Color.LightGray)
              ) {
                  Text(text = "New User? Click Here!", fontSize = 14.sp)
              }
          }
      }
    }
    @Composable
    fun NewUserOnBoardingScreen(navHostController: NavHostController){
        Column(modifier = Modifier.fillMaxSize()){
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                Text(text = "New User Registration", fontWeight = FontWeight.Bold, fontSize = 24.sp, color =LightBlue)
            }
            var userName by remember {
                mutableStateOf("")
            }
            var passWord by remember {
                mutableStateOf("")
            }
            var car_name by remember {
                mutableStateOf(car_name_1)
            }
            Spacer(modifier = Modifier.height(25.dp))
            Row(modifier = Modifier.padding(10.dp)){
                Text(text = "Enter User Name: ", fontWeight = FontWeight.Black, modifier = Modifier.width(100.dp))
                OutlinedTextField(value = userName, onValueChange = {
                    userName = it
                }, modifier = Modifier.padding(5.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            var button_enable by remember {
                mutableStateOf(false)
            }
            Button(onClick = {
                button_enable=true
            }) {
                Text(text = "Select Car")
            }
            if(button_enable) {
                Sheet({button_enable=!button_enable},true)
            }
            Row(modifier = Modifier.padding(10.dp)){
                Text(text = "Enter Password:", fontWeight = FontWeight.Black, modifier = Modifier.width(100.dp))
                OutlinedTextField(
                    value = passWord,
                    onValueChange = { passWord = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.padding(5.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(20.dp), verticalArrangement = Arrangement.Bottom){
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                    Button(onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            println("car_name: "+viewModel.selected_Car!!.car_name)
                            if(userName==""||passWord==""||viewModel.selected_Car!!.car_name==""){
                                withContext(Dispatchers.Main){
                                    Toast.makeText(applicationContext, "Empty Fields Detected", Toast.LENGTH_SHORT).show()
                                }
                            }else {
                                val h = viewModel.newUserRegistration(
                                    user = User(
                                        userName,
                                        passWord,
                                        viewModel.selected_Car?.car_name!!,
                                        viewModel.selected_Car?.company_name!!,
                                        viewModel.selected_Car?.image_path!!
                                    )
                                ).await()
                                var message = ""
                                if (h == null) {
                                    message = "Failed to add User"
                                } else {
                                    if (h) {
                                        message = "New User successfully added!!"
                                    }
                                }
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
                                        .show()
                                    if (h == true) {
                                        navHostController.navigate("login_screen")
                                    }
                                }
                            }
                        }
                    }){
                       Text(text = "Submit")
                    }
                }
            }
        }
    }
    @Composable
    fun IndeterminateCircularIndicator() {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun SingularIcon(
        icon_name: String,
        onClick: () -> Unit,
        onLongClick: () -> Unit,
        onClick3: () -> Unit,
        icon_res: Int
    ) {
        val context = LocalContext.current
        var isShaking by remember { mutableStateOf(false) }
        val compressedBitmap = remember(icon_res) {
            val options = BitmapFactory.Options().apply {
                inSampleSize = 4
            }
            BitmapFactory.decodeResource(context.resources, icon_res, options)
                .asImageBitmap()
        }
        val shakeAnimation = remember { Animatable(0f) }
        LaunchedEffect(isShaking) {
            if (isShaking) {
                while (true) {
                    shakeAnimation.animateTo(
                        targetValue = 10f,
                        animationSpec = tween(durationMillis = 100, easing = LinearEasing)
                    )
                    shakeAnimation.animateTo(
                        targetValue = -10f,
                        animationSpec = tween(durationMillis = 100, easing = LinearEasing)
                    )
                }
            } else {
                shakeAnimation.snapTo(0f)
            }
        }
        Box(
            modifier = Modifier
                .size(100.dp)
                .padding(5.dp)
                .offset(x = shakeAnimation.value.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(0.5.dp, Color.Gray, RoundedCornerShape(20.dp))
                .combinedClickable(
                    onClick = { onClick() },
                    onLongClick = {
                        isShaking = true
                        onLongClick()
                    }
                ),
            contentAlignment = Alignment.TopEnd
        ) {
            Image(
                bitmap = compressedBitmap,
                contentDescription = icon_name,
                modifier = Modifier
                    .size(100.dp)
                    .padding(5.dp)
            )
            if (isShaking) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.White, shape = CircleShape)
                        .padding(4.dp)
                        .clickable {
                            onClick3()
                            isShaking = false
                        }
                )
            }
        }
    }

    @Composable
    fun Home(paddingValues: PaddingValues,navHostController: NavHostController){
        var s by remember {
            mutableStateOf(0)
        }
        LaunchedEffect(true) {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("fleet_users")  // or your collection name
                .get()
                .addOnSuccessListener { querySnapshot ->
                    s = querySnapshot.size()
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Failed to fetch users: ", e)
                }
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(paddingValues), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally){
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(15.dp))
                .height(200.dp)){
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                    Card(modifier = Modifier.padding(10.dp)){
                        Image(painter = painterResource(id = R.drawable.onboarding_1), contentDescription = "null", modifier = Modifier
                            .size(150.dp))
                    }
                    Box(modifier = Modifier
                        .size(150.dp)
                        .padding(5.dp)
                        .border(1.dp, Color.Blue, shape = CircleShape), contentAlignment = Alignment.Center){
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally){
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(text = "Number of Cars:",fontSize =15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text =s.toString(),fontSize =20.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        }
                    }
                }
            }
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(7.dp)){
               CarGridScreen(navHostController = navHostController)
            }
            var s by remember {  mutableStateOf(false) }
            BackHandler {
                s=true
            }
            if(s){
                HandleBackPress({
                    s=false
                })
            }
        }
    }
    @Composable
    fun CarGridScreen(navHostController: NavHostController) {
        val context = LocalContext.current
        val carList = remember { mutableStateListOf<User>() }

        LaunchedEffect(true) {
            val firestore = FirebaseFirestore.getInstance()
            val fleetDataRef = firestore.collection("fleet_users")

            try {
                val snapshot = fleetDataRef.get().await()
                carList.clear()
                for (document in snapshot.documents) {
                    val userName = document.getString("username") ?: ""
                    val carName = document.getString("car_name") ?: ""
                    val password = document.getString("password") ?: ""
                    val companyName = document.getString("company_name") ?: ""
                    val imagePathString = document.getString("imagePath") ?: "0"
                    val imagePath = imagePathString.toIntOrNull() ?: 0
                    val user = User(
                        userName = userName,
                        password = password,
                        car_name = carName,
                        company_name = companyName,
                        imagePath = imagePath
                    )
                    carList.add(user)
                    Log.i("MYTAG",user.toString())
                }
            } catch (e: Exception) {
                Log.e("FirestoreError", "Failed to read car list", e)
            }
        }
        var s by remember { mutableStateOf(false) }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(7.dp),
            contentPadding = PaddingValues(4.dp),
        ) {
            items(carList) { car ->
                SingularIcon(
                    icon_name = car.car_name,
                    onClick = {
                        viewModel.user = car
                        Log.i("MYTAG","clicked on ${car.toString()}")
                        navHostController.navigate("car_info")
                    },
                    {
                        s = !s
                    },
                    {
                        // Optional callback (you had empty)
                    },
                    car.imagePath
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Sheet(onClick: () -> Unit,mode:Boolean = false) : String{
        val s = carList
        val screenHeight = LocalConfiguration.current.screenHeightDp
        val scrollState = rememberScrollState()
        ModalBottomSheet(onDismissRequest = {
            onClick()
        }, modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = (screenHeight / 2).dp)) {
          //add Animation
            Column(modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)){
               s.forEach { car->
                   Card{
                       Row(modifier = Modifier.fillMaxWidth()){
                           Image(painter = painterResource(id = car.image_path!!), contentDescription = "car pic", modifier = Modifier.size(80.dp))
                           Spacer(modifier = Modifier.width(10.dp))
                           Column{
                                   Text(
                                       text = "Car Name: ${car.car_name}",
                                       fontSize = 10.sp,
                                       fontWeight = FontWeight.Bold
                                   )
                                   Text(
                                       text = "Company Name: ${car.company_name}",
                                       fontSize = 10.sp,
                                       fontWeight = FontWeight.Bold
                                   )
                           }
                           Row(modifier = Modifier
                               .fillMaxWidth()
                               .padding(5.dp), horizontalArrangement = Arrangement.End){
                               Checkbox(checked = false, onCheckedChange = {
                                   viewModel.update_2(car)
                                   Toast.makeText(applicationContext, "Selected:${car.car_name}", Toast.LENGTH_SHORT).show()
                                   viewModel.selected_Car=car
                                   if(mode){
                                       onClick()
                                   }
                               })
                           }
                       }
                   }
               }
            }
        }
        return car_name_1
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Dashboard(navHostController: NavHostController) {
        var expand by remember { mutableStateOf(false) }
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var logout by remember { mutableStateOf(false)}
        // Define data class for the navigation items
        data class NavigationItem(
            val title: String,
            val icon: ImageVector,
            val route: String? = null, // Optional route for navigation
            val onClick: (() -> Unit)? = null // Optional custom onClick
        )

        // Create the list of navigation items
        val navigationItems = listOf(
            NavigationItem(
                title = "Home",
                icon = Icons.Filled.Home,
                route = "dashboard" // Route to the dashboard (home)
            ),
            NavigationItem(
                title = "Logout",
                icon = Icons.Filled.ExitToApp,
                onClick = {
                    logout=true
                }
            )
        )
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(Modifier.height(12.dp))
                    navigationItems.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(text = item.title) },
                            selected = false, // Add logic to update the selected item, if necessary
                            onClick = {
                                scope.launch { drawerState.close() }
                                if (item.route != null) {
                                    // If a route is defined, navigate to it
                                    navHostController.navigate(item.route) {
                                        // Optional: Handle pop behavior for navigation
                                        popUpTo(navHostController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                                if (item.onClick != null) {
                                    // If a custom onClick is defined, call it
                                    item.onClick.invoke()
                                }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            icon = {
                                Icon(imageVector = item.icon, contentDescription = item.title)
                            }
                        )
                    }
                }
            },
            gesturesEnabled = true,
            content = {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopBar(onMenuClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        })
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            expand = true
                        }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                ) { innerPadding ->
                    if (expand) {
                        Sheet({ expand = false }, false)
                    }
                    Home(innerPadding, navHostController)
                }
            }
        )
        if(logout){
            HandleLogout({
                logout=false
            },navHostController)
        }
    }

    @Composable
    fun TopBar(onMenuClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .background(LightBlue)
                .height(50.dp)
                .padding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                Icons.Filled.Menu,
                contentDescription = "GetMenu",
                modifier = Modifier
                    .size(24.dp)
                    .padding(2.dp)
                    .clickable {
                        onMenuClick() // Call the provided callback when the menu icon is clicked
                    },
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "DashBoard",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {

                        },
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
        }
    }
    @Composable
    fun BlankPage() {
        val carEntityFlow = remember(viewModel.user!!.userName) { viewModel.getCarEntityFlow() }
        val carEntity by carEntityFlow.collectAsState(initial = null)
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            println(carEntity.toString())
            Spacer(modifier = Modifier.height(20.dp))
            if (carEntity == null) {
                Text(
                    text = "Loading car data...",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                Log.i("MYTAG", "CarEntity: $carEntity")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "User Name: ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = viewModel.user!!.userName ?: "N/A", fontWeight = FontWeight.Normal, fontSize = 15.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Car name: ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = viewModel.user!!.car_name ?: "N/A", fontWeight = FontWeight.Normal, fontSize = 15.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Company name: ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = viewModel.user!!.company_name ?: "N/A", fontWeight = FontWeight.Normal, fontSize = 15.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Pitch: ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "${carEntity?.pitch ?: "N/A"}", fontWeight = FontWeight.Normal, fontSize = 15.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Yaw: ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "${carEntity?.yaw ?: "N/A"}", fontWeight = FontWeight.Normal, fontSize = 15.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Roll: ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "${carEntity?.roll ?: "N/A"}", fontWeight = FontWeight.Normal, fontSize = 15.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Latitude: ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "${carEntity?.latitude ?: "N/A"}", fontWeight = FontWeight.Normal, fontSize = 15.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Longitude: ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "${carEntity?.longitude ?: "N/A"}", fontWeight = FontWeight.Normal, fontSize = 15.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Accel X: ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "${carEntity?.accel_x ?: "N/A"}", fontWeight = FontWeight.Normal, fontSize = 15.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Accel Y: ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "${carEntity?.accel_y ?: "N/A"}", fontWeight = FontWeight.Normal, fontSize = 15.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Accel Z: ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "${carEntity?.accel_z ?: "N/A"}", fontWeight = FontWeight.Normal, fontSize = 15.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Datetime: ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "${carEntity?.datetime ?: "N/A"}", fontWeight = FontWeight.Normal, fontSize = 15.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Timestamp: ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "${carEntity?.timestamp ?: "N/A"}", fontWeight = FontWeight.Normal, fontSize = 15.sp)
                }


            }
        }
    }
}


