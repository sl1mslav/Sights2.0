package com.example.a18hw.data.services

import com.example.a18hw.entity.CloseLocation
import com.example.a18hw.entity.Point
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CloseLocationDto(
    @Json(name = "name") override val name: String,
    @Json(name = "point") override val point: PointDto
): CloseLocation

@JsonClass(generateAdapter = true)
data class PointDto(
    @Json(name = "lon") override val longitude: Double,
    @Json(name="lat") override val latitude: Double
): Point
