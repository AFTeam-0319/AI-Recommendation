package com.example.mynewcode2.ui.view

import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mynewcode2.ui.view.home.InfoInputDialog
import com.example.mynewcode2.ui.view.home.SharedViewModel

data class Place(
    val name: String,
    val address: String,
    val category: String
)

@Composable
fun HomeScreen(
    navController: NavController,
    gender: String,
    age: String,
    peopleCount: String,
    meetingTime: String,
    mealStatus: String,
    location: String,
    onLocationChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onMeetingTimeChange: (String) -> Unit,
    onPeopleCountChange: (String) -> Unit,
    onMealStatusChange: (String) -> Unit,
    viewModel: SharedViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var recentRecords by rememberSaveable { mutableStateOf(listOf("파스타 추천 받음", "근처 카페 검색")) }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column {
                    Text("안녕하세요!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("무엇을 도와드릴까요?", fontSize = 16.sp, color = Color.Gray)
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("recent") }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("최근 기록", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (recentRecords.isEmpty()) {
                            Text("최근 기록이 없습니다.", color = Color.Gray)
                        } else {
                            recentRecords.forEach { record ->
                                Text("• $record", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            item {
                GPTCard(onClick = { navController.navigate("chat/GPT추천") })
            }

            item {
                Column {
                    Text("추천 통계", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• 가장 많이 추천된 장소: 김밥천국")
                    Text("• 평균 나이대: 20대 중반")
                    Text("• 선호 시간대: 오후 6시")
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }

        Button(
            onClick = { showDialog = true },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6200EE),
                contentColor = Color.White
            )
        ) {
            Text("🍽️ 오늘 뭐 먹지?", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        if (showDialog) {
            InfoInputDialog(
                gender = gender,
                age = age,
                meetingTime = meetingTime,
                peopleCount = peopleCount,
                mealStatus = mealStatus,
                onDismiss = { showDialog = false },
                onConfirm = { newGender, newAge, newTime, newCount, newMeal, newLocation ->
                    onGenderChange(newGender)
                    onAgeChange(newAge)
                    onMeetingTimeChange(newTime)
                    onPeopleCountChange(newCount)
                    onMealStatusChange(newMeal)
                    onLocationChange(newLocation) // ✅ 이 줄 추가!

                    // ✅ 추천 로직 실행 (filterPlaces 호출)
                    viewModel.generateRecommendations(
                        age = newAge.toInt(),
                        gender = newGender,
                        peopleCount = newCount.toInt(),
                        mealStatus = newMeal,
                        location = newLocation
                    )

                    showDialog = false
                    recentRecords = listOf("오늘 뭐 먹지? 클릭 → GPT 추천 받음") + recentRecords
                    navController.navigate("chat")
                }
            )
        }
    }
}

@Composable
fun GPTCard(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("💡 GPT 추천 받기", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("AI가 여러분을 위한 장소를 똑똑하게 추천해드려요!", color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { /* 향후 기능 */ },
                modifier = Modifier.align(Alignment.End).padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Text("더 알아보기")
            }
        }
    }
}
