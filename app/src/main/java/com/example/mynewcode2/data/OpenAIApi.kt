package com.example.mynewcode2.data

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIApi {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer sk-" // 여기에 실제 API 키를 넣으세요
    )
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(@Body request: ChatRequest): ChatResponse
}
