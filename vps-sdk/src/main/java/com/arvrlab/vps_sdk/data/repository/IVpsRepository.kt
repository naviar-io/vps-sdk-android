package com.arvrlab.vps_sdk.data.repository

import com.arvrlab.vps_sdk.domain.model.LocalizationModel
import com.arvrlab.vps_sdk.domain.model.VpsLocationModel

internal interface IVpsRepository {

    suspend fun requestLocalization(
        url: String,
        vpsLocationModel: VpsLocationModel
    ): LocalizationModel?

}