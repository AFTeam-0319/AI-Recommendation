package com.example.mynewcode2.ui.view

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mynewcode2.data.filterPlaces
import com.example.mynewcode2.data.findMatchingPlacesFromLocal
import com.example.mynewcode2.data.getRecommendationsFromGPT
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    gender: String,
    age: String,
    peopleCount: String,
    meetingTime: String,
    mealStatus: String,
    location: String,
    navController: NavController
) {
    var input by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<String>() }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // ✅ 중복 요청 방지를 위한 플래그
    var hasRequested by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (gender.isNotBlank() && age.isNotBlank() && meetingTime.isNotBlank()) {
            hasRequested = true

            val userInfoText = """
                📋 사용자 정보
                성별: $gender
                나이: $age
                시간: $meetingTime
                인원 수: $peopleCount
                위치: $location
                식사 유무: $mealStatus
            """.trimIndent()
            messages.add(0, userInfoText)

            isLoading = true

//            try {
//                // GPT 호출 시도
//                val result = getRecommendationsFromGPT(
//                    input = "이 사용자의 정보를 기반으로 적절한 장소 3곳을 추천해주세요.",
//                    gender = gender,
//                    age = age,
//                    meetingTime = meetingTime,
//                    peopleCount = peopleCount,
//                    location = location,
//                    mealStatus = mealStatus
//                )
//                messages.add(0, "🤖 GPT: ${result.joinToString("\n• ", prefix = "• ")}")
//            } catch (e: Exception) {
//                println("❌ GPT 호출 실패: ${e.message}")
//                // 로컬 데이터 fallback
//                val fallbackResult = filterPlaces(
//                    age = age.toInt(),
//                    gender = gender,
//                    personnel = peopleCount.toInt(),
//                    drink = mealStatus == "술 포함",
//                    context = context,
//                    filePath = "places.json"
//                )
//
//                val combinations = fallbackResult["combinations"] as? List<Map<String, Any>> ?: emptyList()
//                println("✅ 로컬 추천 조합 수: ${combinations.size}")
//
//                val selectedCombo = combinations.firstOrNull()
//                if (selectedCombo == null) {
//                    messages.add(0, "🤖 추천할 수 있는 장소가 없습니다.")
//                } else {
//                    val timeline = selectedCombo["timeline"] as? List<*> ?: emptyList<String>()
//                    println("✅ 타임라인: $timeline")
//                    if (timeline.isEmpty()) {
//                        messages.add(0, "🤖 추천할 수 있는 장소가 없습니다.")
//                    } else {
//                        val summary = timeline.joinToString("\n• ", prefix = "• ")
//                        messages.add(0, "🤖 GPT 대신 로컬 데이터로 추천한 장소입니다:\n$summary")
//                    }
//                    Log.d("Timeline", "추천된 일정: $timeline")
//                }
//
//            }
            try {
                val gptResults = getRecommendationsFromGPT(
                    input = "이 사용자의 정보를 기반으로 하루 일정을 추천해줘.",
                    gender = gender,
                    age = age,
                    meetingTime = meetingTime,
                    peopleCount = peopleCount,
                    location = location,
                    mealStatus = mealStatus
                )

                val matchedPlaces = findMatchingPlacesFromLocal(
                    context = context,
                    filePath = "places.json",
                    recommendedNames = gptResults
                )

                if (matchedPlaces.size < 2) {
                    val fallback = filterPlaces(
                        age = age.toInt(),
                        gender = gender,
                        personnel = peopleCount.toInt(),
                        drink = mealStatus == "술 포함",
                        context = context,
                        filePath = "places.json"
                    )

                    val combo = fallback["combinations"] as? List<Map<String, Any>> ?: emptyList()
                    val selected = combo.firstOrNull()
                    val timeline = selected?.get("timeline") as? List<*> ?: listOf<String>()

                    val summary = timeline.joinToString("\n\u2022 ", prefix = "\u2022 ")
                    messages.add(0, "🤖 GPT 결과가 부족해 로컬 추천으로 대체했습니다:\n$summary")
                } else {
                    messages.add(0, "🤖 GPT 추천 결과입니다:\n${gptResults.joinToString("\n\u2022 ", prefix = "\u2022 ")}")
                }
            } catch (e: Exception) {
                println("❌ GPT 오류: ${e.message}")
                val fallback = filterPlaces(
                    age = age.toInt(),
                    gender = gender,
                    personnel = peopleCount.toInt(),
                    drink = mealStatus == "술 포함",
                    context = context,
                    filePath = "places.json"
                )
                val combo = fallback["combinations"] as? List<Map<String, Any>> ?: emptyList()
                val selected = combo.firstOrNull()
                val timeline = selected?.get("timeline") as? List<*> ?: listOf<String>()

                val summary = timeline.joinToString("\n\u2022 ", prefix = "\u2022 ")
                messages.add(0, "🤖 GPT 실패로 로컬 데이터로 추천한 장소입니다:\n$summary")
            }
            isLoading = false
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            items(messages) { msg ->
                val isUserMessage = msg.startsWith("👤 사용자:")
                val displayText = msg.removePrefix("👤 사용자:").removePrefix("🤖 GPT:")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        color = if (isUserMessage) Color(0xFFDCF8C6) else Color(0xFFEFEFEF),
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 2.dp,
                        shadowElevation = 1.dp,
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = displayText.trim(),
                            modifier = Modifier.padding(12.dp),
                            color = Color.Black
                        )
                    }
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            TextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("메시지를 입력하세요") },
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (input.isNotBlank()) {
                        messages.add(0, "👤 사용자: $input")
                        coroutineScope.launch {
                            isLoading = true
                            val response = getRecommendationsFromGPT(
                                input = input,
                                gender = gender,
                                age = age,
                                meetingTime = meetingTime,
                                peopleCount = peopleCount,
                                mealStatus = mealStatus,
                                location = location
                            )
                            messages.add(0, "🤖 GPT: $response")
                            isLoading = false
                        }
                        input = ""
                        keyboardController?.hide()
                    }
                }
            ) {
                Text("전송")
            }
        }
    }
}

