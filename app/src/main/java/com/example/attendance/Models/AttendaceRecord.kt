package com.example.attendance.Models

data class AttendanceRecord(
    val id: Int,
    val userId: Int,
    val username: String,
    val checkIn: String,
    val checkOut: String?,
)