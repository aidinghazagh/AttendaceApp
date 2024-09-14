package com.example.attendance.ViewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendance.Models.LoginRequest
import com.example.attendance.RetrofitClient
import com.example.attendance.StorageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

class LoginViewModel : ViewModel() {
    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()
    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private var _token = ""

    fun onPhoneNumberChange(newPhoneNumber: String) {
        _phoneNumber.value = newPhoneNumber
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun login(onSuccess: () -> Unit, onError: (String) -> Unit, context: Context, phoneNumber: String, password: String) {
        val storageManager = StorageManager(context)
        val loginRequest = LoginRequest(phoneNumber, password)
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.login(loginRequest)
                if (response.status) {
                    val output = response.output
                    _token = output?.get("token").toString()
                    storageManager.saveToken(_token)
                    storageManager.saveUserId((output?.get("userId") as Double).toInt())
                    onSuccess()
                } else {
                    if (response.validations != null){
                        onError("Please provide all fields")
                    }
                    onError(response.errors[0])
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
