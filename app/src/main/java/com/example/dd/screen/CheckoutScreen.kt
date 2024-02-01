package com.example.dd.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.dd.model.CreateResponseModel

@Composable
fun CheckoutScreen(
    onNextClicked: () -> Unit,
    dataMap: CreateResponseModel,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "توضیحات نهایی")
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        PaddingValues(
                            horizontal = 16.dp, vertical = 16.dp
                        )
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AsyncImage(
                    modifier = Modifier.size(200.dp),
                    model = dataMap.imageUrl,
                    contentDescription = "Translated description of what the image contains"
                )
                ListItemView("batteryHealth", dataMap.batteryHealth)
                ListItemView("availableMemory", dataMap.availableMemory)
                ListItemView("totalMemory", dataMap.totalMemory)
                ListItemView("deviceModel", dataMap.deviceModel)
                ListItemView("deviceBrand", dataMap.deviceBrand)
                ListItemView("deviceBoard", dataMap.deviceBoard)
                ListItemView("deviceHardware", dataMap.deviceHardware)
                ListItemView("deviceManufacturer", dataMap.deviceManufacturer)
                ListItemView("deviceProduct", dataMap.deviceProduct)
                ListItemView("osVersion", dataMap.osVersion)
            }
            Button(onClick = onNextClicked) {
                Text(text = "ارسال و بازگشت به دیوار")
            }
        }
    }
}

@Composable
fun ListItemView(key: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = key, fontSize = 14.sp)
        Text(text = value, fontSize = 14.sp)
    }
}