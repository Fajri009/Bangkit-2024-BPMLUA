package com.example.bangkit_2024_bpmlua.KonsepDasarAndroiduntukMachineLearning.MengunggahBerkaskeServer.Retrofit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainRetrofit: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://reqres.in/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        val client = apiService.getListUsers("1")

        client.enqueue(object: Callback<ResponseUser> {
            override fun onResponse(call: Call<ResponseUser>, response: Response<ResponseUser>) {
                if (response.isSuccessful) {
                    val listAser = response.body()?.data as List<DataItem>
                    // listUser siap digunakan untuk digunakan.
                }
            }

            override fun onFailure(call: Call<ResponseUser>, error: Throwable) {
                // Terpanggil ketika tidak dapat menghubungi server.
            }
        })
    }
}