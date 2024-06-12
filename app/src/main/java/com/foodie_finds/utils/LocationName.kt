package com.foodie_finds.utils

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LocationName (
    val place_id: String,
    val formatted_address: String
)

data class LocationNameResult (
    @SerializedName("results") val results: Array<LocationName>,
): Serializable
