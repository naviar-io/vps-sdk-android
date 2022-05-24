package com.arvrlab.vps_sdk.domain.interactor

import com.arvrlab.vps_sdk.data.LocalizationType
import com.arvrlab.vps_sdk.data.model.CameraIntrinsics
import com.arvrlab.vps_sdk.domain.model.*

internal interface IVpsInteractor {

    suspend fun prepareVpsLocationModel(
        source: ByteArray,
        sessionId: String,
        localizationType: LocalizationType,
        cameraPose: NodePoseModel,
        gpsLocation: GpsLocationModel? = null,
        compass: CompassModel,
        cameraIntrinsics: CameraIntrinsics
    ): VpsLocationModel

    suspend fun calculateNodePose(
        url: String,
        vpsLocationModel: VpsLocationModel
    ): LocalizationModel?

    fun destroy()

}