package com.example.foodie_finds.utils

import com.example.foodie_finds.utils.LocationName
import retrofit2.http.GET
import retrofit2.http.Query


interface LocationNameApi {
    @GET("geocode/json")
    suspend fun getLocationName(
        @Query("location_type") locationType: String,
        @Query("result_type") resultType: String,
        @Query("key") apiKey: String,
        @Query("latlng") latlng: String
    ): LocationNameResult
}