package com.arvrlab.vps_sdk.data.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ResponseDataModel(
    @Json(name = "id")
    val id: String,
    @Json(name = "attributes")
    val attributes: ResponseAttributesModel
)
