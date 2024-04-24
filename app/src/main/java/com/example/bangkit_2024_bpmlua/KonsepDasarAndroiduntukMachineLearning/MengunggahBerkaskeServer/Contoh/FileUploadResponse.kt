package com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.MengunggahBerkaskeServer.Contoh

import com.google.gson.annotations.SerializedName

data class FileUploadResponse(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String
)
