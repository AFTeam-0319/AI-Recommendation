package com.example.mynewcode2.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.mynewcode2.data.OpenAIApi

object RetrofitClient {
    private const val BASE_URL = "https://api.openai.com/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val openAIApi: OpenAIApi = retrofit.create(OpenAIApi::class.java)
}
