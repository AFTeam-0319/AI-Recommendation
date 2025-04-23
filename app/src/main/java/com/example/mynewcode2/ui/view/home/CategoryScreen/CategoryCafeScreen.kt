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
fun CategoryCafeScreen(navController: NavController) {
    val cafeList = listOf(
        "스타벅스" to "https://source.unsplash.com/featured/?starbucks",
        "이디야" to "https://source.unsplash.com/featured/?ediya",
        "커피빈" to "https://source.unsplash.com/featured/?coffeebean",
        "탐앤탐스" to "https://source.unsplash.com/featured/?tomntoms",
        "빽다방" to "https://source.unsplash.com/featured/?paikdabang",
        "할리스" to "https://source.unsplash.com/featured/?hollys",
        "커피프렌즈" to "https://source.unsplash.com/featured/?coffee",
        "카페베네" to "https://source.unsplash.com/featured/?cafebene"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "☕ 카페 추천",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "오늘은 어떤 카페에 가고 싶어요?",
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
            items(cafeList) { (name, imageUrl) ->
                CafeCard(name, imageUrl)
            }
        }
    }
}

@Composable
fun CafeCard(name: String, imageUrl: String) {
    val context = LocalContext.current // context 가져오기

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable {
                Toast.makeText(context, "$name 카페 선택됨", Toast.LENGTH_SHORT).show()
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
