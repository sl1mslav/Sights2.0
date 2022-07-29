package com.example.a18hw.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Gallery")
data class Photo(
    @PrimaryKey
    @ColumnInfo(name="filepath")
    val filepath: String,
    @ColumnInfo(name="date")
    val date: String
)