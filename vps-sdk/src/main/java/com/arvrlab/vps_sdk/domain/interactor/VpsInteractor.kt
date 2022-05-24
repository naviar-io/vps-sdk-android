package com.arvrlab.vps_sdk.domain.interactor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import com.arvrlab.vps_sdk.data.LocalizationType
import com.arvrlab.vps_sdk.data.MobileVps
import com.arvrlab.vps_sdk.data.Photo
import com.arvrlab.vps_sdk.data.model.CameraIntrinsics
import com.arvrlab.vps_sdk.data.repository.IPrefsRepository
import com.arvrlab.vps_sdk.data.repository.IVpsRepository
import com.arvrlab.vps_sdk.domain.model.*
import com.arvrlab.vps_sdk.util.Constant.BITMAP_WIDTH
import com.arvrlab.vps_sdk.util.Constant.MATRIX_ROTATE
import com.arvrlab.vps_sdk.util.Constant.QUALITY
import com.arvrlab.vps_sdk.util.TimestampUtil
import com.arvrlab.vps_sdk.util.cropTo16x9
import com.arvrlab.vps_sdk.util.toGrayscale
import java.io.ByteArrayOutputStream

internal class VpsInteractor(
    private val vpsRepository: IVpsRepository,
    private val neuroInteractor: INeuroInteractor,
    private val prefsRepository: IPrefsRepository
) : IVpsInteractor {

    private var scaleFactorPhoto: Float = 1f

    override suspend fun prepareVpsLocationModel(
        source: ByteArray,
        sessionId: String,
        localizationType: LocalizationType,
        cameraPose: NodePoseModel,
        gpsLocation: GpsLocationModel?,
        compass: CompassModel,
        cameraIntrinsics: CameraIntrinsics
    ): VpsLocationModel {
        val byteArray = convertByteArray(source, localizationType)
        val newCameraIntrinsics = cameraIntrinsics.scaleCameraIntrinsics(localizationType)

        return VpsLocationModel(
            sessionId = sessionId,
            userId = prefsRepository.getUserId(),
            timestamp = TimestampUtil.getTimestampInSec(),
            gpsLocation = gpsLocation,
            compass = compass,
            trackingPose = cameraPose,
            localizationType = localizationType,
            byteArray = byteArray,
            cameraIntrinsics = newCameraIntrinsics
        )
    }

    override suspend fun calculateNodePose(
        url: String,
        vpsLocationModel: VpsLocationModel
    ): LocalizationModel? =
        vpsRepository.requestLocalization(url, vpsLocationModel)

    override fun destroy() {
        neuroInteractor.close()
    }

    private suspend fun convertByteArray(
        source: ByteArray,
        localizationType: LocalizationType
    ): ByteArray =
        when (localizationType) {
            is Photo -> createJpgByteArray(source)
            is MobileVps -> {
                neuroInteractor.loadNeuroModel(localizationType)
                createNeuroByteArray(source)
            }
        }

    private suspend fun createNeuroByteArray(byteArray: ByteArray): ByteArray {
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return neuroInteractor.codingBitmap(bitmap)
    }

    private fun createJpgByteArray(byteArray: ByteArray): ByteArray {
        val source = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            .cropTo16x9()
        scaleFactorPhoto = BITMAP_WIDTH.toFloat() / source.width

        val matrix = Matrix()
            .apply {
                postRotate(MATRIX_ROTATE)
                postScale(scaleFactorPhoto, scaleFactorPhoto)
            }
        val bitmap = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
            .toGrayscale()

        return ByteArrayOutputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, stream)
            stream.toByteArray()
        }
    }

    private fun CameraIntrinsics.scaleCameraIntrinsics(localizationType: LocalizationType): CameraIntrinsics {
        val scale = when (localizationType) {
            is Photo -> scaleFactorPhoto
            is MobileVps -> neuroInteractor.scaleFactorImage
        }
        return this.copy(
            fx = fx * scale,
            fy = fy * scale,
            cx = cx * scale,
            cy = cy * scale
        )
    }
}