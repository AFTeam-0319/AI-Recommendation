package com.example.mynewcode2.ui

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.mynewcode2.ui.view.ChatScreen
import com.example.mynewcode2.ui.view.WebViewScreen
import com.example.mynewcode2.ui.view.HomeScreen
import com.example.mynewcode2.ui.view.home.GPTInfoScreen
import com.example.mynewcode2.ui.view.home.InfoInputDialog
import com.example.mynewcode2.ui.view.home.RecordScreen
import com.example.mynewcode2.ui.view.home.SharedViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf("home", "chat", "web")
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    var recentRecords by remember { mutableStateOf(listOf("파스타 추천 받음", "근처 카페 검색")) }
    // 동적 데이터 상태
    var gender by rememberSaveable { mutableStateOf("남성") }
    var age by rememberSaveable { mutableStateOf("") }
    var meetingTime by rememberSaveable { mutableStateOf("") }
//    var peopleCount by rememberSaveable { mutableStateOf("2") }
    var peopleCount by rememberSaveable { mutableStateOf("2") }
    var location by rememberSaveable { mutableStateOf("") } // 위치 상태
//    // location을 Location? 타입으로 변경
//    var location by rememberSaveable { mutableStateOf<Location?>(null) }

    var mealStatus by rememberSaveable { mutableStateOf("") } // 식사 유무 상태

    val onPeopleCountChange: (String) -> Unit = { newCount ->
        peopleCount = newCount
    }

    val sharedViewModel: SharedViewModel = viewModel()

    // showDialog 상태 관리 (다이얼로그 표시 여부)
    var showDialog by rememberSaveable { mutableStateOf(false) }

    // 취소 버튼 눌렀을 때 상태 초기화
    val onDismiss = {
        showDialog = false
        // 상태 초기화 (필요시)
        gender = "남성"
        age = ""
        meetingTime = ""
        peopleCount = "2"
//        location = "" // 위치 초기화
//        mealStatus = "" // 식사 유무 초기화
    }

    // 확인 버튼 눌렀을 때 상태 전달
    val onConfirm = {
        showDialog = false
        // 확인 후 상태를 ChatScreen이나 다른 화면에 전달하는 로직
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("설정", modifier = Modifier.padding(16.dp))
                Divider()
                Text("계정", modifier = Modifier.padding(16.dp))
                Text("알림", modifier = Modifier.padding(16.dp))
                Text("도움말", modifier = Modifier.padding(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (currentRoute == "home") {  // 홈 화면에서만 TopAppBar 표시
                    TopAppBar(
                        title = { Text("홈 화면") },
                        actions = {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = "메뉴") // 햄버거 아이콘
                            }
                        }
                    )
                }
            },
            bottomBar = {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            selected = currentRoute == screen,
                            onClick = {
                                if (currentRoute != screen) {
                                    navController.navigate(screen) {
                                        if (screen == "home") {
                                            popUpTo("home") { inclusive = true }
                                        } else {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            },
                            icon = {
                                when (screen) {
                                    "home" -> Text("🏠") // 홈 화면
                                    "web" -> Text("🗺️") // 지도
                                    "chat" -> Text("💬") // 채팅
                                    else -> Text("🔹") // 기본 아이콘
                                }
                            },
                            label = { Text(screen.replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavHost(navController, startDestination = "home", Modifier.padding(paddingValues)) {
                composable("home") {
                    HomeScreen(
                        navController = navController,
                        gender = gender,
                        age = age,
                        peopleCount = peopleCount, // ✅ 추가
                        meetingTime = meetingTime,
                        location = location, // 위치 전달
                        mealStatus = mealStatus, // 식사 유무 전달
                        onGenderChange = { gender = it },
                        onAgeChange = { age = it },
                        onMeetingTimeChange = { meetingTime = it },
                        onPeopleCountChange = onPeopleCountChange, // 전달
                        onLocationChange = { location = it }, // 위치 변경 처리
                        onMealStatusChange = { mealStatus = it } // 식사 유무 변경 처리
                    )

                }
                composable("chat") {
                    ChatScreen(
                        gender = gender,
                        age = age,
                        meetingTime = meetingTime,
                        peopleCount = peopleCount, // 전달
                        mealStatus = mealStatus, // 식사 유무 전달
                        location = location, // 👈 이걸 추가해줘야 함
                        navController = navController
                    )
                }
                composable("web") { WebViewScreen() }
                composable("recent") { RecordScreen(navController) }
                composable("gptInfo") { GPTInfoScreen(navController) }
            }
        }
    }
    if (showDialog) {
        InfoInputDialog(
            gender = gender,
            age = age,
            meetingTime = meetingTime,
            peopleCount = peopleCount,
            mealStatus = mealStatus, // 식사 유무 전달
            onDismiss = onDismiss,  // 취소 버튼 눌렀을 때 상태 초기화
            onConfirm = { newGender, newAge, newMeetingTime, newPeopleCount, newMealStatus, newLocation ->
                // InfoInputDialog에서 받은 새로운 값들로 상태 업데이트
                gender = newGender
                age = newAge
                meetingTime = newMeetingTime
                peopleCount = newPeopleCount
                mealStatus = newMealStatus
                location = newLocation // ✅ 여기가 중요함!!

                // ViewModel에도 저장 (추가 예정)
                sharedViewModel.setUserInfo(
                    gender, age, meetingTime, peopleCount, mealStatus, location
                )

                // location은 InfoInputDialog 내에서 처리되고, 여기서 받게 됨
                println("선택된 위치: $location")

                // Dialog 닫고, 이후 로직 처리
                showDialog = false
                recentRecords = listOf("오늘 뭐 먹지? 클릭 → GPT 추천 받음") + recentRecords
                navController.navigate("chat")


            }
        )
    }

}

