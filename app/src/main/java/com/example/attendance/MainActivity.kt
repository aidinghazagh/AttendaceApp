package com.example.attendance

import android.content.Context
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.attendance.Screens.HomeScreen
import com.example.attendance.Screens.LoginScreen
import com.example.attendance.ViewModels.HomeViewModel
import com.example.attendance.ViewModels.LoginViewModel
import com.example.attendance.ui.theme.AttendanceTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AttendanceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Set up the NavController
                    val navController = rememberNavController()
                    NavGraph(navController, LocalContext.current)
                }
            }
        }
    }
}

@Composable
fun NavGraph(navController: NavHostController, context: Context) {
    val storageManager = StorageManager(context)
    var startDestination = NavRoutes.Login.route
    if (storageManager.getToken() != null){
        startDestination = NavRoutes.Home.route
    }
    val loginViewModel : LoginViewModel = viewModel()
    val homeViewModel : HomeViewModel = viewModel()
     NavHost(navController = navController, startDestination = startDestination) {
        composable(NavRoutes.Login.route) { LoginScreen(loginViewModel ,navController) }
        composable(NavRoutes.Home.route) { HomeScreen(homeViewModel, navController) }
    }
}
