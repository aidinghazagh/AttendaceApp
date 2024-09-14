package com.example.attendance

import com.example.attendance.Models.AttendanceRecord
import com.example.attendance.Models.LoginRequest
import com.example.attendance.Models.ResponseList
import com.example.attendance.Models.ResponseList2
import com.example.attendance.Models.ResponseMap
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest, @Header("ACCEPT") accept: String): Call<ResponseMap>


    @GET("attendance-records")
    fun getAttendanceRecords(@Query("offset") offset: Int, @Header("Authorization") token: String, @Header("Accept") accept : String, @Query("selected") selected: String): Call<ResponseList<AttendanceRecord>>

    @POST("logout")
    fun logout(@Header("Authorization") token: String, @Header("ACCEPT") accept: String) : Call<ResponseMap>

    @POST("check-in")
    fun checkIn(@Header("Authorization") token: String, @Header("ACCEPT") accept: String) : Call<ResponseMap>

    @POST("check-out")
    fun checkOut(@Header("Authorization") token: String, @Header("ACCEPT") accept: String) : Call<ResponseMap>
}
