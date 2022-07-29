package com.example.a18hw.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.a18hw.entity.Photo

@Database(entities = [Photo::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun photoDao() : PhotoDao
}