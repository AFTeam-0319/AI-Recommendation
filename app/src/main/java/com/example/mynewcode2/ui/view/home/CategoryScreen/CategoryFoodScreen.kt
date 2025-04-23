package com.example.mynewcode2.ui.view.home.CategoryScreen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun CategoryFoodScreen(navController: NavController) {
    val foodList = listOf(
        "김치찌개" to "https://source.unsplash.com/featured/?kimchi",
        "마라탕" to "https://source.unsplash.com/featured/?malatang",
        "돈까스" to "https://source.unsplash.com/featured/?porkcutlet",
        "파스타" to "https://source.unsplash.com/featured/?pasta",
        "초밥" to "https://source.unsplash.com/featured/?sushi",
        "떡볶이" to "https://source.unsplash.com/featured/?tteokbokki",
        "햄버거" to "https://source.unsplash.com/featured/?burger",
        "비빔밥" to "https://source.unsplash.com/featured/?bibimbap"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "🍜 음식 추천",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "오늘 이런 음식 어때요?",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(foodList) { (name, imageUrl) ->
                FoodCard(name, imageUrl)
            }
        }
    }
}

@Composable
fun FoodCard(name: String, imageUrl: String) {
    val context = LocalContext.current // 여기에서 context를 가져와야 함.

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable {
                Toast.makeText(context, "$name 선택됨", Toast.LENGTH_SHORT).show()
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(110.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

