package com.example.a18hw.entity

interface CloseLocation {
    val name: String
    val point: Point
}

interface Point {
    val longitude: Double
    val latitude: Double
}