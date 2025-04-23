package com.example.mynewcode2.data

import retrofit2.HttpException
import com.example.mynewcode2.network.RetrofitClient

suspend fun getRecommendationsFromGPT(
    input: String,
    gender: String,
    age: String,
    meetingTime: String,
    peopleCount: String, // 여기에 추가
    mealStatus: String,  // 여기에 추가
    location: String
): List<String> {
    val chatRequest = ChatRequest(
        messages = listOf(
            Message(role = "system", content = "You are a helpful assistant."),
            Message(
                role = "user",
                content = """
                    사용자 정보:
                    - 성별: $gender
                    - 나이: $age
                    - 만나는 시간: $meetingTime
                    - 인원 수: $peopleCount
                    - 식사 유무: $mealStatus
                    - 위치: $location

                    위 정보를 바탕으로 하루 일정을 구성해서 추천해줘.
                    장소는 총 3곳 정도로 하고, 시간 순서대로 어떤 장소에서 어떤 활동을 하면 좋을지 알려줘.
                    각 장소는 왜 추천하는지 간단한 설명도 붙여줘.
                    
                    다음 형식으로 출력해줘:
                    • 14:00 - 15:00: XX카페 – 친구들과 대화 나누기 좋은 분위기
                    • 15:30 - 17:00: OO공원 – 산책하며 여유를 즐기기 좋아요
                    • 17:30 - 19:00: YY식당 – 저녁식사 및 가벼운 음주
                    
                    너무 길게 말하지 말고, 꼭 시간 순서대로 간단명료하게 추천해줘.
                """.trimIndent()
            )
        )
    )

    return try {
        val response = RetrofitClient.openAIApi.getChatCompletion(chatRequest)
        println("✅ GPT 응답 수신 완료: $response")

        val recommendationText = response.choices.firstOrNull()?.message?.content ?: ""
        println("✅ GPT 추천 텍스트: $recommendationText")

        // '•' 기준으로 분리
        recommendationText
            .split("\n")
            .filter { it.trim().startsWith("•") }
            .map { it.removePrefix("•").trim() }

    } catch (e: HttpException) {
        if (e.code() == 429) {
            println("❗요청 한도 초과(429): OpenAI 사용량 또는 결제 상태를 확인하세요.")
        } else {
            println("❗HTTP 오류 발생: 코드 ${e.code()} - ${e.message()}")
        }
        listOf("결제 하셔야 됩니다.")
    } catch (e: Exception) {
        println("❌ 알 수 없는 오류 발생: ${e.message}")
        e.printStackTrace()
//        listOf("알 수 없는 오류 발생")
        throw e
    }
}