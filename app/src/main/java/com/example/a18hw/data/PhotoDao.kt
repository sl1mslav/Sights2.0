package com.example.a18hw.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.a18hw.entity.Photo
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    // Gets all of the photos from the Database
    @Query("SELECT * FROM Gallery")
    fun getAll(): Flow<List<Photo>>

    // Adds a photo to the Gallery
    @Insert
    suspend fun insert(photo: Photo)

    // Deletes a specific item from the database.
    @Delete
    suspend fun clearData(photo: Photo)
}