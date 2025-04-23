package com.example.mynewcode2.ui.view.home

import android.Manifest // 이 줄을 사용하여 android.Manifest를 가져옵니다.
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.threeten.bp.LocalTime
import java.util.Locale
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun InfoInputDialog(
    gender: String,
    age: String,
    meetingTime: String,
    peopleCount: String,
    mealStatus: String,
    onConfirm: (
        gender: String,
        age: String,
        meetingTime: String,
        peopleCount: String,
        mealStatus: String,
        location: String
    ) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTime by remember { mutableStateOf(meetingTime) }

    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    var localGender by remember { mutableStateOf(gender) }
    var localAge by remember { mutableStateOf(age) }
    var localPeopleCount by remember { mutableStateOf(peopleCount) }
    var localMeetingTime by remember { mutableStateOf(meetingTime) }
    var localMealStatus by remember { mutableStateOf(mealStatus) }

    var city by remember { mutableStateOf("서울") }
    var district by remember { mutableStateOf("종로구") }

    // 위치 가져오기 (권한이 있을 경우에만)
    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status is PermissionStatus.Granted) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            )
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                try {
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            val geocoder = Geocoder(context, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                            addresses?.firstOrNull()?.let { address ->
                                city = address.locality ?: "서울시"
                                district = address.subLocality ?: "종로구"
                                Log.d("Location", "자동 설정된 위치: $city $district") // 디버깅 로그 추가
                            }
                        }
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val location = "$city $district"
                Log.d("Location", "확인된 위치: $location") // 확인 시 위치 값 로그 출력
                onConfirm(
                    localGender,
                    localAge,
                    localMeetingTime,
                    localPeopleCount,
                    localMealStatus,
                    location
                )
            }) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        },
        title = { Text("기본 정보 입력", style = MaterialTheme.typography.titleLarge) },
        text = {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                item {
                    Text("성별", style = MaterialTheme.typography.bodyLarge)
                    GenderToggleButtons(
                        selectedGender = localGender,
                        onGenderChange = { localGender = it }
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    Text("나이", style = MaterialTheme.typography.bodyLarge)
                    AgePicker(selectedAge = localAge.toIntOrNull() ?: 20) {
                        localAge = it.toString()
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                // 만나 시간 선택기 추가
                item {
                    Text("만나는 시간", style = MaterialTheme.typography.bodyLarge)
                    TimePickerDialog(
                        initialTime = localMeetingTime,
                        onTimeChange = { newTime ->
                            localMeetingTime = newTime
                        }
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    Text("인원수", style = MaterialTheme.typography.bodyLarge)
                    PeoplePicker(selectedCount = localPeopleCount.toIntOrNull() ?: 2) {
                        localPeopleCount = it.toString()
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    Text("위치 (시, 구)", style = MaterialTheme.typography.bodyLarge)
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("시") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = district,
                        onValueChange = { district = it },
                        label = { Text("구") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

//                item {
//                    TimePickerDialog(selectedTime) { newTime ->
//                        selectedTime = newTime
//                        onMeetingTimeChange(newTime)
//                    }
//                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    Text("식사 여부", style = MaterialTheme.typography.bodyLarge)
                    MealStatusToggleButtons(
                        selectedMealStatus = localMealStatus,
                        onMealStatusChange = { localMealStatus = it }
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}



@Composable
fun MealStatusToggleButtons(
    selectedMealStatus: String,
    onMealStatusChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        MealStatusButton(
            label = "식사 안 함",
            isSelected = selectedMealStatus == "식사 안 함",
            onClick = { onMealStatusChange("식사 안 함") }
        )
        MealStatusButton(
            label = "식사 함",
            isSelected = selectedMealStatus == "식사 함",
            onClick = { onMealStatusChange("식사 함") }
        )
    }
}

@Composable
private fun RowScope.MealStatusButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Black

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.dp)
    ) {
        Text(text = label, color = textColor)
    }
}

@Composable
fun GenderToggleButtons(
    selectedGender: String,
    onGenderChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        GenderButton(
            label = "남성",
            isSelected = selectedGender == "남성",
            onClick = { onGenderChange("남성") }
        )
        GenderButton(
            label = "여성",
            isSelected = selectedGender == "여성",
            onClick = { onGenderChange("여성") }
        )
    }
}

@Composable
private fun RowScope.GenderButton( // ✅ RowScope로 확장
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Black

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.dp)
    ) {
        Text(text = label, color = textColor)
    }
}

@Composable
fun AgePicker(selectedAge: Int, onAgeChange: (Int) -> Unit) {
    val ages = (10..80).toList()
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = ages.indexOf(selectedAge)
    )

    LaunchedEffect(listState.firstVisibleItemIndex) {
        // 중앙에 있는 항목을 선택한 항목으로 간주하여 상태 변경
        val middleIndex = listState.layoutInfo.visibleItemsInfo.size / 2
        val middleItem = listState.layoutInfo.visibleItemsInfo[middleIndex]
        if (middleItem.index in ages.indices) {
            onAgeChange(ages[middleItem.index])
        }
    }

    Box(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .height(100.dp),
            reverseLayout = false // 스크롤을 위에서 아래로 기본 설정
        ) {
            items(ages) { age ->
                val isSelected = age == selectedAge

                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "$age 세",
                        style = TextStyle(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = if (isSelected) 24.sp else 16.sp // 선택된 숫자는 크게, 두껍게
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    initialTime: String,
    onTimeChange: (String) -> Unit
) {

    val currentTime = LocalTime.now() // 현재 시간 가져오기
    val currentHour = currentTime.hour
    var currentMinute = currentTime.minute

    // 현재 시간을 5분 단위로 조정 (가장 가까운 5의 배수로 설정)
    currentMinute = (currentMinute / 5) * 5

    // 현재 시간 및 분을 기본값으로 설정
    var hour by remember { mutableStateOf(currentHour) }
    var minute by remember { mutableStateOf(currentMinute) }

//    var hour by remember { mutableStateOf(getTimeComponent(initialTime, 0)) }
//    var minute by remember { mutableStateOf(getTimeComponent(initialTime, 1)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 시간 선택 텍스트
        Text(
            text = "시간 선택",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 시간 다이얼 (시, 분)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfiniteWheelSelector(
                items = (0..23).toList(),
                selectedItem = hour,
                onItemSelected = { selectedHour ->
                    hour = selectedHour
                    onTimeChange(String.format("%02d:%02d", hour, minute))
                }
            )
            InfiniteWheelSelector(
                items = (0..59 step 5).toList(), // 5분 단위
                selectedItem = minute,
                onItemSelected = { selectedMinute ->
                    minute = selectedMinute
                    onTimeChange(String.format("%02d:%02d", hour, minute))
                }
            )
        }
    }
}

fun getTimeComponent(initialTime: String, index: Int): Int {
    val timeParts = initialTime.split(":")
    return if (timeParts.size > index) {
        timeParts[index].toIntOrNull() ?: 0
    } else {
        0
    }
}

@Composable
fun InfiniteWheelSelector(
    items: List<Int>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    val repeatedItems = List(100) { items }.flatten()
    val itemCount = items.size
    val itemHeight = 40.dp
    val listHeight = 120.dp
    val centerPadding = (listHeight - itemHeight) / 2
    val centerIndexOffset = 0

    val initialIndex = repeatedItems.indexOfFirst { it == selectedItem } + itemCount * 50
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val centerIndex = listState.firstVisibleItemIndex + centerIndexOffset
            val value = repeatedItems.getOrNull(centerIndex) ?: return@LaunchedEffect
            onItemSelected(value)

            coroutineScope.launch {
                listState.animateScrollToItem(centerIndex - centerIndexOffset)
            }
        }
    }

    Box(
        modifier = Modifier
            .height(listHeight)
            .width(80.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = centerPadding),
            modifier = Modifier.height(listHeight)
        ) {
            itemsIndexed(repeatedItems) { index, item ->
                val isSelected = listState.firstVisibleItemIndex + centerIndexOffset == index
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.toString().padStart(2, '0'),
                        fontSize = if (isSelected) 24.sp else 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
    }
}

@Composable
fun PeoplePicker(selectedCount: Int, onCountChange: (Int) -> Unit) {
    val counts = (1..20).toList() // 기본 리스트 범위 (1~20명)
    val repeatedItems = List(100) { counts }.flatten()
    val itemCount = counts.size
    val itemHeight = 40.dp
    val listHeight = 120.dp
    val centerPadding = (listHeight - itemHeight) / 2
    val centerIndexOffset = 0

    val initialIndex = repeatedItems.indexOfFirst { it == selectedCount } + itemCount * 50
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val centerIndex = listState.firstVisibleItemIndex + centerIndexOffset
            val value = repeatedItems.getOrNull(centerIndex) ?: return@LaunchedEffect
            onCountChange(value)

            coroutineScope.launch {
                listState.animateScrollToItem(centerIndex - centerIndexOffset)
            }
        }
    }

    Box(
        modifier = Modifier
            .height(listHeight)
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = centerPadding),
            modifier = Modifier.height(listHeight)
        ) {
            itemsIndexed(repeatedItems) { index, item ->
                val isSelected = listState.firstVisibleItemIndex + centerIndexOffset == index
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$item 명",
                        fontSize = if (isSelected) 24.sp else 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}




//@Preview
//@Composable
//fun PreviewInfoInputDialog() {
//    InfoInputDialog(
//        gender = "남성",
//        age = "28",
//        meetingTime = "14:00",
//        peopleCount = "2",
////        location = "서울 강남구", // 기본 위치 값 추가
//        mealStatus = "식사 함",  // 기본 식사 유무 값 추가
//        onGenderChange = {},
//        onAgeChange = {},
//        onMeetingTimeChange = {},
//        onPeopleCountChange = {},
////        onLocationChange = {}, // 위치 변경 함수
//        onMealStatusChange = {}, // 식사 유무 변경 함수
//        onDismiss = {}
//    ) {}
//}

@Preview(showBackground = true)
@Composable
fun PreviewInfoInputDialog() {
    InfoInputDialog(
        gender = "남성",
        age = "28",
        meetingTime = "14:00",
        peopleCount = "2",
        mealStatus = "식사 함",
        onConfirm = { gender, age, time, people, meal, location ->
            // 미리보기에서는 단순 로그 출력 등으로 대체 가능
            println("gender: $gender, age: $age, time: $time, people: $people, meal: $meal, location: $location")
        },
        onDismiss = {}
    )
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionWrapper() {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    var locationText by remember { mutableStateOf("위치를 가져오는 중...") }

    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status is PermissionStatus.Granted) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)

                        if (addresses.isNullOrEmpty()) {
                            locationText = "위치 정보를 찾을 수 없음"
                        } else {
                            val address = addresses.firstOrNull()
                            if (address != null) {
                                val city = address.locality ?: "알 수 없음"
                                val district = address.subLocality ?: "알 수 없음"
                                locationText = "$city $district"
                            } else {
                                locationText = "주소를 찾을 수 없음"
                            }
                        }
                    } ?: run {
                        locationText = "위치를 가져올 수 없음"
                    }
                }.addOnFailureListener {
                    locationText = "위치 정보를 가져오는 데 실패했습니다"
                }

            } catch (e: SecurityException) {
                locationText = "권한이 필요합니다"
            } catch (e: Exception) {
                locationText = "위치 정보를 가져오는데 실패했습니다"
            }
        }
    }

    Column {
        when (locationPermissionState.status) {
            is PermissionStatus.Granted -> {
                Text("현재 위치: $locationText", fontSize = 16.sp)
            }
            is PermissionStatus.Denied -> {
                Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                    Text("위치 권한 요청")
                }
                Text("권한이 필요합니다", color = Color.Red)
            }
        }
    }
}



