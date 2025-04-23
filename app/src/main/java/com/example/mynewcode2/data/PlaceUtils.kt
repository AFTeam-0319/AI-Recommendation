package com.example.mynewcode2.data

import android.content.Context

fun findMatchingPlacesFromLocal(
    context: Context,
    filePath: String,
    recommendedNames: List<String>
): List<Place> {
    val places = loadPlaces(context, filePath)
    return recommendedNames.mapNotNull { name ->
        places.find { it.name.contains(name, ignoreCase = true) }
    }
}
