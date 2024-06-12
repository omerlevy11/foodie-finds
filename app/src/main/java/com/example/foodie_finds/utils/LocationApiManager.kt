package com.example.foodie_finds.utils

import com.example.foodie_finds.utils.LocationNameApi
import com.example.foodie_finds.utils.LocationNameResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class LocationApiManager {
    private val restLocationApi = "https://maps.googleapis.com/maps/api/"

    private val api = Retrofit.Builder().baseUrl(restLocationApi)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create()).build().create(LocationNameApi::class.java)

    suspend fun getLocationName(lat : String, lng : String): LocationNameResult {
        val locationType = "ROOFTOP"
        val resultType = "street_address"
        val apiKey = "AIzaSyCFZA4zakv-y2YEsQF2nQuWtAyXxM9UCTM"
        val latlng = "${lat},${lng}"

        return withContext(Dispatchers.IO) {
            api.getLocationName(locationType, resultType, apiKey, latlng)
        }
    }
}