package com.appetiser.data.entities

import com.google.gson.annotations.SerializedName

data class MovieResponse<DTO>(
    @SerializedName("resultCount")
    val resultCount: Int,
    @SerializedName("results")
    val results: DTO
)
