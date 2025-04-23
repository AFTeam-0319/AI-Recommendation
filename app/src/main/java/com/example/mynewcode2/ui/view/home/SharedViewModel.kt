package com.example.mynewcode2.ui.view.home

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import com.example.mynewcode2.data.filterPlaces

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@RequiresApi(Build.VERSION_CODES.O)
class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val _recommendationResult = MutableStateFlow<Map<String, Any>?>(null)
    val recommendationResult: StateFlow<Map<String, Any>?> = _recommendationResult

    // ✅ 사용자 정보 저장
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo

    // ✅ MainScreen에서 이 함수 호출 가능
    fun setUserInfo(
        gender: String,
        age: String,
        meetingTime: String,
        peopleCount: String,
        mealStatus: String,
        location: String
    ) {
        _userInfo.value = UserInfo(gender, age, meetingTime, peopleCount, mealStatus, location)
    }

    fun generateRecommendationsFromUserInfo() {
        val info = _userInfo.value ?: return
        val context = getApplication<Application>().applicationContext
        val result = filterPlaces(
            age = info.age.toInt(),
            gender = info.gender,
            personnel = info.peopleCount.toInt(),
            drink = info.mealStatus == "술 포함",
            context = context,
            filePath = "places.json"
        )
        _recommendationResult.value = result
    }

//    // 기존 방식도 계속 사용 가능
//    fun generateRecommendations(
//        age: Int,
//        gender: String,
//        peopleCount: Int,
//        mealStatus: String,
//        location: String
//    ) {
//        val context = getApplication<Application>().applicationContext
//        val drink = mealStatus == "술 포함"
//        val result = filterPlaces(
//            age = age,
//            gender = gender,
//            personnel = peopleCount,
//            drink = drink,
//            context = context,
//            filePath = "places/places.json"
//        )
//        _recommendationResult.value = result
//    }
fun generateRecommendations(
    age: Int,
    gender: String,
    peopleCount: Int,
    mealStatus: String,
    location: String
) {
    val context = getApplication<Application>().applicationContext
    val drink = mealStatus == "술 포함"

    // ✅ API 26 이상일 때만 실행
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val result = filterPlaces(
            age = age,
            gender = gender,
            personnel = peopleCount,
            drink = drink,
            context = context,
            filePath = "places.json"
        )
        _recommendationResult.value = result
    } else {
        // API 26 미만에서는 에러 처리 또는 기본 메시지
        _recommendationResult.value = mapOf(
            "error" to "이 기능은 Android 8.0 (API 26) 이상에서만 지원됩니다."
        )
    }
}

}

// ✅ 사용자 정보를 담기 위한 데이터 클래스
data class UserInfo(
    val gender: String,
    val age: String,
    val meetingTime: String,
    val peopleCount: String,
    val mealStatus: String,
    val location: String
)

