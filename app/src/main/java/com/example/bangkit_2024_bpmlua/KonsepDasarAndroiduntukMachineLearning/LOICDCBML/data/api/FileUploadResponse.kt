package com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.LOICDCBML.data.api

data class FileUploadResponse(
	val data: Data? = null,
	val message: String? = null
)

data class Data(
	val result: String? = null,
	val createdAt: String? = null,
	val confidenceScore: Any? = null,
	val isAboveThreshold: Boolean? = null,
	val id: String? = null
)

