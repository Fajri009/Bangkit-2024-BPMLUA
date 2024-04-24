package com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.LOICDCBML.data.api

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("skin-cancer/predict")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): FileUploadResponse
}