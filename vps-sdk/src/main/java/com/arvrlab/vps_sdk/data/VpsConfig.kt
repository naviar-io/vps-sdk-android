package com.arvrlab.vps_sdk.data

data class VpsConfig(
    val vpsUrl: String,
    val intervalLocalizationMS: Long = 2500,
    val useGps: Boolean = false,
    val localizationType: LocalizationType = MobileVps(),
    val worldInterpolationDurationMS: Long = 500,
    val worldInterpolationDistanceLimit: Float = 2f,
    val worldInterpolationAngleLimit: Float = 10f
) {
    companion object {

        fun getIndoorConfig(vpsUrl: String): VpsConfig =
            VpsConfig(
                vpsUrl = vpsUrl,
                useGps = false
            )

        fun getOutdoorConfig(vpsUrl: String): VpsConfig =
            VpsConfig(
                vpsUrl = vpsUrl,
                useGps = true
            )

    }
}