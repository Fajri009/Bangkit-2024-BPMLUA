package com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.MengunggahBerkaskeServer.Retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // Menambahkan informasi menggunakan Header
    // @Headers : menambahkan informasi tambahan pada header request, seperti authorization, jenis data, dsb.
    // @Query : memasukkan parameter pada method @GET.
    @Headers("Authorization: token <Personal Access Token>")
    @GET("api/users")
    fun getListUsers(@Query("page") page: String): Call<ResponseUser>

    // Mendapatkan daftar pengguna berdasarkan id dengan path.
    // @Path : memasukkan variabel yang dapat diubah-ubah pada endpoint. Sebagai contoh parameter id akan mengganti {id} yang ada di endpoint.
    @GET("api/users/{id}")
    fun getUser(@Path("id") id: String): Call<ResponseUser>

    // Mengirim data pengguna menggunakan x-www-form-urlencoded.
    // @FormUrlEncoded : menandai fungsi pada @POST sebagai form-url-encoded.
    // @Field : memasukkan parameter pada method @POST.
    // Urlencoded digunakan untuk mengirim sekumpulan pasangan key-value ke server
    @FormUrlEncoded
    @POST("api/users")
    fun createUser(
        @Field("name") name: String,
        @Field("job") job: String
    ): Call<ResponseUser>

    // Mengunggah berkas menggunakan Multipart
    // @Multipart : menandai sebuah fungsi bahwa ia merupakan multipart.
    // @Part : mengirimkan berkas dengan cara multipart.
    // @PartMap : mengirimkan data lain selain berkas pada multipart.
    // Sama seperti Urlencoded tapi multipart mendukung lebih dari satu kumpulan data yang digabungkan dalam satu body.
    @Multipart
    @PUT("api/uploadfile")
    fun updateUser(
        @Part("file") file: MultipartBody.Part,
        @PartMap data: Map<String, RequestBody>
    ): Call<ResponseUser>
}