package com.example.mynewcode2.ui.view.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun GPTInfoScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("GPT 정보", style = MaterialTheme.typography.headlineMedium)
        // GPT 관련 정보를 표시할 수 있는 UI 추가
        Text("GPT는 다양한 추천을 제공할 수 있습니다.")
        Text("예시: 음식 추천, 여행지 추천 등")
    }
}
