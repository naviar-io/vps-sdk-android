package com.arvrlab.vps_sdk.data.repository

import com.arvrlab.vps_sdk.data.MobileVps
import com.arvrlab.vps_sdk.data.Photo
import com.arvrlab.vps_sdk.data.api.IVpsApiManager
import com.arvrlab.vps_sdk.data.model.CompassModel
import com.arvrlab.vps_sdk.data.model.GpsModel
import com.arvrlab.vps_sdk.data.model.LocationModel
import com.arvrlab.vps_sdk.data.model.PoseModel
import com.arvrlab.vps_sdk.data.model.request.RequestAttributesModel
import com.arvrlab.vps_sdk.data.model.request.RequestDataModel
import com.arvrlab.vps_sdk.data.model.request.RequestIntrinsicsModel
import com.arvrlab.vps_sdk.data.model.request.RequestVpsModel
import com.arvrlab.vps_sdk.domain.model.GpsPoseModel
import com.arvrlab.vps_sdk.domain.model.LocalizationModel
import com.arvrlab.vps_sdk.domain.model.NodePoseModel
import com.arvrlab.vps_sdk.domain.model.VpsLocationModel
import com.arvrlab.vps_sdk.util.Logger
import com.squareup.moshi.JsonAdapter
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

internal class VpsRepository(
    private val vpsApiManager: IVpsApiManager,
    private val requestVpsAdapter: JsonAdapter<RequestVpsModel>,
) : IVpsRepository {

    private companion object {
        const val STATUS_DONE = "done"

        const val EMBEDDING = "embedding"
        const val IMAGE = "image"
        const val JSON = "json"

        val ANY_MEDIA_TYPE = "*/*".toMediaTypeOrNull()
        val IMAGE_MEDIA_TYPE = "image/jpeg".toMediaTypeOrNull()
        val JSON_MEDIA_TYPE = "application/json".toMediaTypeOrNull()
    }

    override suspend fun requestLocalization(
        url: String,
        vpsLocationModel: VpsLocationModel
    ): LocalizationModel? {
        val vpsApi = vpsApiManager.getVpsApi(url)

        val jsonBody = vpsLocationModel.toRequestVpsModel()
            .toBodyPart()
        val contentBody = when (vpsLocationModel.localizationType) {
            is Photo -> vpsLocationModel.toBodyPart(IMAGE_MEDIA_TYPE, IMAGE)
            is MobileVps -> vpsLocationModel.toBodyPart(ANY_MEDIA_TYPE, EMBEDDING)
        }
        val response = try {
            vpsApi.requestLocalization(jsonBody, contentBody)
        } catch (e: Exception) {
            Logger.error(e)
            return null
        }

        val attributesModel = response.data?.attributes
        if (attributesModel?.status == STATUS_DONE) {
            val vpsPoseModel = attributesModel.vpsPose
                .toNodePoseModel()
            val trackingPoseModel = attributesModel.trackingPose
                .toNodePoseModel()
            val gpsPoseModel = attributesModel.location
                .toGpsPoseModel()
            return LocalizationModel(vpsPoseModel, trackingPoseModel, gpsPoseModel)
        }
        return null
    }

    private fun VpsLocationModel.toRequestVpsModel(): RequestVpsModel =
        RequestVpsModel(
            data = RequestDataModel(
                attributes = RequestAttributesModel(
                    sessionId = this.sessionId,
                    userId = this.userId,
                    timestamp = this.timestamp,
                    location = this.gpsLocation?.let { gpsLocation ->
                        LocationModel(
                            gps = GpsModel(
                                accuracy = gpsLocation.accuracy,
                                altitude = gpsLocation.altitude,
                                latitude = gpsLocation.latitude,
                                longitude = gpsLocation.longitude,
                                timestamp = gpsLocation.elapsedTimestampSec
                            ),
                            compass = CompassModel(
                                accuracy = compass.accuracy,
                                heading = compass.heading,
                                timestamp = compass.timestamp
                            )
                        )
                    },
                    trackingPose = PoseModel(
                        x = this.trackingPose.x,
                        y = this.trackingPose.y,
                        z = this.trackingPose.z,
                        rx = this.trackingPose.rx,
                        ry = this.trackingPose.ry,
                        rz = this.trackingPose.rz
                    ),
                    intrinsics = RequestIntrinsicsModel(
                        cx = cameraIntrinsics.cx,
                        cy = cameraIntrinsics.cy,
                        fx = cameraIntrinsics.fx,
                        fy = cameraIntrinsics.fy,
                    )
                )
            )
        )

    private fun RequestVpsModel.toBodyPart(): MultipartBody.Part {
        val requestBody = requestVpsAdapter.toJson(this).toRequestBody(JSON_MEDIA_TYPE)
        return MultipartBody.Part.createFormData(JSON, null, requestBody)
    }

    private fun VpsLocationModel.toBodyPart(
        contentType: MediaType?,
        name: String,
        fileName: String? = name
    ): MultipartBody.Part {
        val requestBody = byteArray.toRequestBody(contentType, 0, byteArray.size)
        return MultipartBody.Part.createFormData(name, fileName, requestBody)
    }

    private fun PoseModel?.toNodePoseModel(): NodePoseModel =
        if (this == null)
            NodePoseModel.DEFAULT
        else
            NodePoseModel(
                x = this.x,
                y = this.y,
                z = this.z,
                rx = this.rx,
                ry = this.ry,
                rz = this.rz,
            )

    private fun LocationModel?.toGpsPoseModel(): GpsPoseModel =
        if (this == null)
            GpsPoseModel.EMPTY
        else
            GpsPoseModel(
                altitude = gps.altitude,
                latitude = gps.latitude,
                longitude = gps.longitude,
                heading = compass?.heading ?: 0f
            )

}