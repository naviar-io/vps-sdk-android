package com.arvrlab.vps_sdk.data.model.response

import com.arvrlab.vps_sdk.data.model.LocationModel
import com.arvrlab.vps_sdk.data.model.PoseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ResponseAttributesModel(
    @Json(name = "status")
    val status: String,
    @Json(name = "location")
    val location: LocationModel?,
    @Json(name = "client_coordinate_system")
    val clientCoordinateSystem: String?,
    @Json(name = "tracking_pose")
    val trackingPose: PoseModel?,
    @Json(name = "vps_pose")
    val vpsPose: PoseModel?
)
