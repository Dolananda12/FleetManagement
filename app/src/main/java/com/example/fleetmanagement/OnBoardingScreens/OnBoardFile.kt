package com.example.fleetmanagement.OnBoardingScreens
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fleetmanagement.FleetOnboardObject

@Composable
fun OnBoardItem(item : FleetOnboardObject){
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top){
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp
        if(item.imageRes!=null){
        Image(painter = painterResource(id = item.imageRes), contentDescription = "OnBoardingScreen", modifier = Modifier
            .fillMaxWidth()
            .height((0.75 * (screenHeight)).dp))
            Text(
                text = item.title, style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color =Color.Blue,
                    textAlign = TextAlign.Center,
                )
            )
            Text(
                text = item.description,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                )
            )
        }else{
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                Text(text = item.title, fontWeight = FontWeight.Bold, color = Color.Blue, fontSize = 24.sp)
            }
        }

    }
}
