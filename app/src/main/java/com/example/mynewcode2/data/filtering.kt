package com.example.mynewcode2.data

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

data class OpeningHour(
    val open: String?,
    val close: String?,
    val last_order: String?,
    val break_time: String?
)

data class Place(
    val name: String,
    val cat: String,
    val cat2: String,
    val id: String,
    val review_keywords: List<String>?,
    val coordinate1: String,
    val coordinate2: String,
    val hours: Map<String, OpeningHour>?
)

@RequiresApi(Build.VERSION_CODES.O)
fun parseTime(timeStr: String): LocalTime {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return LocalTime.parse(timeStr, formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun isPlaceOpen(place: Place, now: LocalTime, weekday: String): Boolean {
    val hours = place.hours ?: return true
    val dayHours = hours[weekday] ?: hours["\uC560\uC77C"] ?: return true

    val openTime = dayHours.open?.let { parseTime(it) }
    val closeTime = dayHours.last_order?.let { parseTime(it) } ?: dayHours.close?.let { parseTime(it) }

    if (openTime == null || closeTime == null) return true

    if (now.isBefore(openTime) || now.isAfter(closeTime)) return false

    val breakTime = dayHours.break_time ?: ""
    if ("-" in breakTime) {
        val breakTimes = breakTime.split("-").map { it.trim() }
        val startBreak = parseTime(breakTimes[0])
        val endBreak = parseTime(breakTimes[1])
        if (now.isAfter(startBreak) && now.isBefore(endBreak)) {
            return false
        }
    }

    return true
}

fun categorizeAge(age: Int): String {
    return when {
        age < 10 -> "u10s"
        age < 20 -> "10s"
        age < 30 -> "20s"
        age < 40 -> "30s"
        age < 50 -> "40s"
        age < 60 -> "50s"
        age < 70 -> "60s"
        else -> "over60s"
    }
}

fun categorizePersonnel(count: Int): String {
    return when {
        count == 1 -> "1"
        count <= 3 -> "2~3"
        count <= 6 -> "4~6"
        count <= 8 -> "7~8"
        else -> "over8"
    }
}

fun getPlaceDuration(cat: String): Int {
    return when {
        "\uC2DD\uB2F9" in cat -> 60
        "\uCE74\uD398" in cat -> 90
        "\uB180\uAC8C\uB9CC\uC5D0" in cat -> 120
        "\uC220\uC9D1" in cat -> 120
        else -> 0
    }
}

fun loadPlaces(context: Context, filePath: String): List<Place> {
    return try {
        val json = context.assets.open(filePath).bufferedReader().use { it.readText() }
        val gson = Gson()
        val type = object : TypeToken<List<Place>>() {}.type
        gson.fromJson(json, type)
    } catch (e: Exception) {
        Log.e("LoadPlaces", "\uC624\uB958 \uBC1C\uC0DD: ${e.message}")
        emptyList()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun filterPlaces(age: Int, gender: String, personnel: Int, drink: Boolean, context: Context, filePath: String): Map<String, Any> {
    val now = LocalTime.now()
    val weekday = listOf("\uC6D4", "\uD654", "\uC218", "\uBAA9", "\uAE08", "\uD1A0", "\uC77C")[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1]

    val places = loadPlaces(context, filePath)
    Log.d("FilterPlaces", "총 로드된 장소 수: ${places.size}")

    val ageCategory = categorizeAge(age)
    val personnelCategory = categorizePersonnel(personnel)

    val filtered = places.filter { place ->
        if ("\uC220\uC9D1" in place.cat && (age < 19 || !drink)) return@filter false
        if (!isPlaceOpen(place, now, weekday)) return@filter false
        true
    }

    val groupedByCat = filtered.groupBy { it.cat }

    val combinations = mutableListOf<Map<String, Any>>()
    repeat(5) {
        val selected = mutableListOf<Place>()
        val usedIds = mutableSetOf<String>()

        groupedByCat.forEach { (_, items) ->
            val available = items.filter { it.id !in usedIds }
            if (available.isNotEmpty()) {
                val choice = available.random()
                usedIds.add(choice.id)
                selected.add(choice)
            }
        }

        if (selected.isEmpty()) return@repeat

        val timelineSteps = mutableListOf<String>()
        var timeCursor = now
        var totalMinutes = 0

        selected.forEachIndexed { idx, p ->
            val duration = getPlaceDuration(p.cat)
            val startTime = timeCursor
            val endTime = startTime.plusMinutes(duration.toLong())
//            timelineSteps.add("$startTime - $endTime: ${p.name}")
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val startFormatted = startTime.format(formatter)
            val endFormatted = endTime.format(formatter)
            timelineSteps.add("$startFormatted - $endFormatted: ${p.name}")

            timeCursor = endTime
            if (idx != selected.size - 1) {
                timeCursor = timeCursor.plusMinutes(15)
                totalMinutes += 15
            }
            totalMinutes += duration
        }

        combinations.add(
            mapOf(
                "places" to selected.map {
                    mapOf(
                        "name" to it.name,
                        "cat2" to it.cat2,
                        "review_keywords" to it.review_keywords,
                        "id" to it.id,
                        "coordinate1" to it.coordinate1,
                        "coordinate2" to it.coordinate2
                    )
                },
                "timeline" to timelineSteps,
                "estimated_time" to "${totalMinutes / 60}h ${totalMinutes % 60}m"
            )
        )
    }

    Log.d("FilterPlaces", "남은 장소 수: ${filtered.size}")
    Log.d("FilterPlaces", "카테고리별 분포: ${groupedByCat.mapValues { it.value.size }}")
    Log.d("FilterPlaces", "조합 수: ${combinations.size}")

    return mapOf("combinations" to combinations)
}
