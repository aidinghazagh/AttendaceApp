package com.example.attendance.Screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendance.ViewModels.LoginViewModel
import com.example.attendance.NavRoutes

@Composable
fun LoginScreen(viewModel: LoginViewModel, navController: NavController) {
    val context = LocalContext.current
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = phoneNumber,
            onValueChange = { viewModel.onPhoneNumberChange(it)},
            label = { Text("Phone number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(onClick = {
                viewModel.login(
                    onSuccess = {
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()
                        navController.navigate(NavRoutes.Home.route) {
                            popUpTo(NavRoutes.Login.route) { inclusive = true }
                        }
                    },
                    onError = {
                        error ->
                        Toast.makeText(context, "Login failed: $error", Toast.LENGTH_LONG).show()
                        println(error)
                    },
                    context,
                    phoneNumber,
                    password)
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Login")
            }
        }
    }
}

