package com.arvrlab.vps_sdk.data.api

import com.arvrlab.vps_sdk.data.model.response.ResponseVpsModel
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

internal interface VpsApi {

    @Multipart
    @POST("vps/api/v2")
    suspend fun requestLocalization(@Part vararg parts: MultipartBody.Part): ResponseVpsModel

}