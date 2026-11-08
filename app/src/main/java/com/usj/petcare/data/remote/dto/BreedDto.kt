package com.usj.petcare.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BreedDto(
    val id: Int,
    val name: String,
    val temperament: String? = null,

    @SerializedName("life_span")
    val lifeSpan: String? = null,

    @SerializedName("breed_group")
    val breedGroup: String? = null,

    @SerializedName("reference_image_id")
    val referenceImageId: String? = null,

    val image: BreedImageDto? = null
)

data class BreedImageDto(
    val id: String? = null,
    val url: String? = null
)
