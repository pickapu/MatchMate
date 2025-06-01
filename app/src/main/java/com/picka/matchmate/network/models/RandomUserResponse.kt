package com.picka.matchmate.network.models

import com.google.gson.annotations.SerializedName

data class RandomUserResponse(
    @SerializedName("results")
    val results: List<RandomUserDto>
)

data class RandomUserDto(
    @SerializedName("gender")
    val gender: String,

    @SerializedName("name")
    val name: NameDto,

    @SerializedName("location")
    val location: LocationDto,

    @SerializedName("dob")
    val dob: DobDto,

    @SerializedName("picture")
    val picture: PictureDto
)

data class NameDto(
    @SerializedName("title")
    val title: String,

    @SerializedName("first")
    val first: String,

    @SerializedName("last")
    val last: String
)

data class LocationDto(
    @SerializedName("city")
    val city: String
)

data class DobDto(
    @SerializedName("age")
    val age: Int
)

data class PictureDto(
    @SerializedName("large")
    val large: String
)
