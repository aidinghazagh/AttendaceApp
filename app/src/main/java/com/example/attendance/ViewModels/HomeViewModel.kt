package com.example.attendance.ViewModels


import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendance.Models.AttendanceRecord
import com.example.attendance.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _attendanceRecords = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val attendanceRecords = _attendanceRecords.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _isLogoutLoading = MutableStateFlow(false)
    val isLogoutLoading = _isLogoutLoading.asStateFlow()

    private val _hasUnchecked = MutableStateFlow(false)
    val hasUnchecked = _hasUnchecked.asStateFlow()

    private val _isActionButtonLoading = MutableStateFlow(false)
    val isActionButtonLoading = _isActionButtonLoading.asStateFlow()

    private val _isExtraLoading = MutableStateFlow(false)
    val isExtraLoading = _isExtraLoading.asStateFlow()

    private val _dropDownSelected = MutableStateFlow("All")
    val dropDownSelected = _dropDownSelected.asStateFlow()

    // Function to update the selected filter
    fun updateSelectedFilter(selected: String) {
        _dropDownSelected.value = selected
    }

    fun fetchAttendanceRecords(token : String, fullRefresh: Boolean = false, userId: Int){
        viewModelScope.launch {
            try {
                if (fullRefresh){
                    _attendanceRecords.value = emptyList()
                    _isLoading.value = true
                } else{
                    _isExtraLoading.value = true
                }
                val response = RetrofitClient.getAttendanceRecords(_attendanceRecords.value.size, token, _dropDownSelected.value)
                if (response.status) {
                    val output = response.output!!
                        if (fullRefresh){
                            _attendanceRecords.value = output
                        } else{
                            _attendanceRecords.value += output
                        }
                    for (record in _attendanceRecords.value){
                        if (record.userId == userId && record.checkOut == null){
                            _hasUnchecked.value = true
                            break
                        } else{
                            _hasUnchecked.value = false
                        }
                    }
                    _errorMessage.value = null // Clear any previous error message
                } else {
                    if (response.validations != null){
                        _errorMessage.value = "Please provide all fields"
                    }
                    _errorMessage.value = response.errors[0]
                }
            } catch (e: Exception) {
                // Handle other errors
                _errorMessage.value = "Error: ${e.message}"
                println(_errorMessage.value)
            } finally {
                if (fullRefresh){
                    _isLoading.value = false
                } else{
                    _isExtraLoading.value =false
                }
            }
        }
    }
    fun logout(onSuccess: () -> Unit, token : String){
        viewModelScope.launch {
            try {
                _isLogoutLoading.value = true
                val response = RetrofitClient.logout(token)
                println("Response: $response")
                if (response.status) {
                    onSuccess()
                } else {
                    _errorMessage.value = response.errors[0]
                }
            } catch (e: Exception) {
                // Handle other errors
                _errorMessage.value = "Error: ${e.message}"
                println(_errorMessage.value)
            } finally {
                _isLogoutLoading.value = false
            }
        }
    }
    fun checkIn(token: String, userId: Int){
        viewModelScope.launch {
            try {
                _isActionButtonLoading.value = true
                val response = RetrofitClient.checkIn(token)
                if (response.status){
                    fetchAttendanceRecords(token, fullRefresh = true, userId)
                } else{
                    _errorMessage.value = response.errors[0]
                }
            } catch (e: Exception){
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isActionButtonLoading.value = false
            }
        }
    }
    fun checkOut(token: String, userId: Int){
        viewModelScope.launch {
            try {
                _isActionButtonLoading.value = true
                val response = RetrofitClient.checkOut(token)
                if (response.status){
                    fetchAttendanceRecords(token, fullRefresh = true, userId)
                } else{
                    _errorMessage.value = response.errors[0]
                }
            } catch (e: Exception){
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isActionButtonLoading.value = false
            }
        }
    }
}
