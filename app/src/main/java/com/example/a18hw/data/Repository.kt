package com.example.a18hw.data

import com.example.a18hw.data.services.CloseLocationDto
import com.example.a18hw.data.services.RetrofitInstance
import com.example.a18hw.entity.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class Repository @Inject constructor(
    private val photoDao: PhotoDao
) {
    fun getPhotosFromDatabase(): Flow<List<Photo>> {
        return photoDao.getAll()
    }

    suspend fun insertPhoto(filepath: String, date: String) {
        photoDao.insert(Photo(filepath, date))
    }

    suspend fun deletePhotosFromDatabase(allPhotos: StateFlow<List<Photo>>) {
        allPhotos.value.forEach {
            photoDao.clearData(it)
        }
    }

    suspend fun getCloseLocationsFromAPI(latitude: Double, longitude: Double): List<CloseLocationDto> {
        return RetrofitInstance.openTripMapAPI.getCloseSightsFromAPI(
            latitude = latitude,
            longitude = longitude
        )
    }
}