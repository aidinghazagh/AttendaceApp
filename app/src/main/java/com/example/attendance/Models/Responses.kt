package com.example.attendance.Models


data class ResponseList2(
    val status: Boolean,
    val output: Any?
)

data class ResponseList<T>(
    val status: Boolean,                // The success status of the request
    val output: List<T>? = emptyList(), // List or or null
    val errors: List<String> = listOf(""),           // List of error messages
    val validations: Map<String, Any>?  // Null or a map of validation errors

)
data class ResponseMap(
    val status: Boolean,                // The success status of the request
    val output: Map<String, Any>?, // List or or null
    val errors: List<String>,           // List of error messages
    val validations: Map<String, Any>?  // Null or a map of validation errors
)
