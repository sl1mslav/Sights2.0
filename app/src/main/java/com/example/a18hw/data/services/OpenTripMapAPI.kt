package com.example.a18hw.data.services

import com.example.a18hw.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object RetrofitInstance {
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.TRIP_MAP_API_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val openTripMapAPI: OpenTripMapAPI = retrofit.create(OpenTripMapAPI::class.java)
}

interface OpenTripMapAPI {
    @GET("radius")
    suspend fun getCloseSightsFromAPI(
        @Query("radius") radius: Int = 2000,
        @Query("lon") longitude: Double,
        @Query("lat") latitude: Double,
        @Query("src_geom") srcGeom: String = "wikidata",
        @Query("src_attr") srcAttr: String = "wikidata",
        @Query("format") format: String = "json",
        @Query("apikey") apiKey: String = Constants.TRIP_MAP_API_TOKEN
    ): List<CloseLocationDto>
}