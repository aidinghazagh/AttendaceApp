package com.example.attendance

import com.example.attendance.Models.AttendanceRecord
import com.example.attendance.Models.LoginRequest
import com.example.attendance.Models.ResponseList
import com.example.attendance.Models.ResponseMap
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000/api/" // Use 10.0.2.2 for localhost
    private const val ACCEPT = "application/json"
    private const val BEARER = "Bearer "
    private val retrofit: Retrofit =	Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder() .setLenient() .create()))
        .build()

    suspend fun login(loginRequest: LoginRequest): ResponseMap {
        val api: ApiService = retrofit.create(ApiService::class.java)
        val call: Call<ResponseMap> = api.login(loginRequest, ACCEPT)

        return call.await()
    }
    suspend fun getAttendanceRecords(offset: Int, token: String, selected: String): ResponseList<AttendanceRecord> {
        val api: ApiService = retrofit.create(ApiService::class.java)
        val call: Call<ResponseList<AttendanceRecord>> = api.getAttendanceRecords(offset, BEARER + token, ACCEPT, selected)

        return call.await()
    }
    suspend fun logout(token : String) : ResponseMap{
        val api: ApiService = retrofit.create(ApiService::class.java)
        val call: Call<ResponseMap> = api.logout(BEARER + token, ACCEPT)

        return call.await()
    }
    suspend fun checkIn(token: String) : ResponseMap{
        val api: ApiService = retrofit.create(ApiService::class.java)
        val call: Call<ResponseMap> = api.checkIn(BEARER + token, ACCEPT)
        return call.await()
    }
    suspend fun checkOut(token: String) : ResponseMap{
        val api: ApiService = retrofit.create(ApiService::class.java)
        val call: Call<ResponseMap> = api.checkOut(BEARER + token, ACCEPT)
        return call.await()
    }

}