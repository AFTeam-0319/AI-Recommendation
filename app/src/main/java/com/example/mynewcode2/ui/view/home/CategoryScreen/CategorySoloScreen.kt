package com.example.mynewcode2.ui.view.home.CategoryScreen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun CategorySoloScreen(navController: NavController) {
    val soloFoodList = listOf(
        "김밥" to "https://source.unsplash.com/featured/?kimbap",
        "라면" to "https://source.unsplash.com/featured/?ramen",
        "분식" to "https://source.unsplash.com/featured/?bunsik",
        "초밥" to "https://source.unsplash.com/featured/?sushi",
        "치킨" to "https://source.unsplash.com/featured/?chicken",
        "햄버거" to "https://source.unsplash.com/featured/?burger",
        "떡볶이" to "https://source.unsplash.com/featured/?tteokbokki",
        "볶음밥" to "https://source.unsplash.com/featured/?friedrice"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "🍚 혼밥 추천",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "혼자서 즐길 수 있는 음식 추천",
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
            items(soloFoodList) { (name, imageUrl) ->
                SoloFoodCard(name, imageUrl)
            }
        }
    }
}

@Composable
fun SoloFoodCard(name: String, imageUrl: String) {
    val context = LocalContext.current // context 가져오기

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
