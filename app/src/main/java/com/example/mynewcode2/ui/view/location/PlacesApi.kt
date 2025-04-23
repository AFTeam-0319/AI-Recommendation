//package com.example.mynewcode2.ui.view.location
//
//import com.example.mynewcode2.ui.view.Place
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.GET
//import retrofit2.http.Header
//import retrofit2.http.Query
//
//// 네이버 API를 위한 인터페이스
//interface NaverPlacesApi {
//    @GET("v1/search/local.json")
//    suspend fun getNearbyRestaurants(
//        @Query("query") query: String,
//        @Query("coordinate") coordinate: String,
//        @Query("radius") radius: Int,
//        @Header("X-Naver-Client-Id") clientId: String,
//        @Header("X-Naver-Client-Secret") clientSecret: String
//    ): NaverPlacesResponse
//}
//
//
//// 네이버 API에서 반환되는 응답 모델
//data class NaverPlacesResponse(val items: List<Place>)
//data class NaverPlace(val title: String, val address: String, val category: String)
//
//suspend fun getNearbyPlaces(location: String): List<Place> {
//    val clientId = "YOUR_NAVER_CLIENT_ID"
//    val clientSecret = "YOUR_NAVER_CLIENT_SECRET"
//    val retrofit = Retrofit.Builder()
//        .baseUrl("https://openapi.naver.com/")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    val naverPlacesApi = retrofit.create(NaverPlacesApi::class.java)
//
//    val response = naverPlacesApi.getNearbyRestaurants(
//        query = "맛집",
//        coordinate = location,
//        radius = 500,
//        clientId = clientId,
//        clientSecret = clientSecret
//    )
//
//    return response.items
//}
//

package com.example.mynewcode2.ui.view.location

import android.util.Log
import com.example.mynewcode2.ui.view.Place
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.HttpException
import java.io.IOException

// 네이버 API를 위한 인터페이스
interface NaverPlacesApi {
    @GET("v1/search/local.json")
    suspend fun getNearbyRestaurants(
        @Query("query") query: String,
        @Query("coordinate") coordinate: String,
        @Query("radius") radius: Int,
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String
    ): NaverPlacesResponse
}

// 네이버 API에서 반환되는 응답 모델
data class NaverPlacesResponse(val items: List<Place>)
data class NaverPlace(val title: String, val address: String, val category: String)

suspend fun getNearbyPlaces(location: String): List<Place> {
    val clientId = "YOUR_NAVER_CLIENT_ID" // 네이버 개발자 사이트에서 발급받은 client ID
    val clientSecret = "YOUR_NAVER_CLIENT_SECRET" // 네이버 개발자 사이트에서 발급받은 client Secret

    // Retrofit 설정
    val retrofit = Retrofit.Builder()
        .baseUrl("https://openapi.naver.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val naverPlacesApi = retrofit.create(NaverPlacesApi::class.java)

    return try {
        // 네이버 API 호출
        val response = naverPlacesApi.getNearbyRestaurants(
            query = "맛집",
            coordinate = location,
            radius = 500,
            clientId = clientId,
            clientSecret = clientSecret
        )

        // 성공적으로 데이터를 가져온 경우
        response.items

    } catch (e: HttpException) {
        // HTTP 오류 처리 (예: 401 Unauthorized, 500 Internal Server Error 등)
        Log.e("API Error", "HttpException: ${e.message()}")
        emptyList()  // 오류가 발생하면 빈 리스트 반환 (UI에 적절한 오류 메시지 표시)
    } catch (e: IOException) {
        // 네트워크 연결 오류 처리
        Log.e("API Error", "IOException: ${e.message}")
        emptyList()  // 네트워크 오류 발생 시 빈 리스트 반환
    } catch (e: Exception) {
        // 그 외의 일반적인 예외 처리
        Log.e("API Error", "Unknown error: ${e.message}")
        emptyList()  // 예외가 발생하면 빈 리스트 반환
    }
}
