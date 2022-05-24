package com.arvrlab.vps_sdk.data.model.request

import com.arvrlab.vps_sdk.data.model.LocationModel
import com.arvrlab.vps_sdk.data.model.PoseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RequestAttributesModel(
    @Json(name = "session_id")
    val sessionId: String,
    @Json(name = "user_id")
    val userId: String,
    @Json(name = "timestamp")
    val timestamp: Double,
    @Json(name = "location")
    val location: LocationModel?,
    @Json(name = "client_coordinate_system")
    val clientCoordinateSystem: String = "arcore",
    @Json(name = "tracking_pose")
    val trackingPose: PoseModel,
    @Json(name = "intrinsics")
    val intrinsics: RequestIntrinsicsModel
)
