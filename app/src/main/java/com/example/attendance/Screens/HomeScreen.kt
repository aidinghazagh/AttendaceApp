package com.example.attendance.Screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendance.NavRoutes
import com.example.attendance.StorageManager
import com.example.attendance.ViewModels.HomeViewModel
import com.example.attendance.composables.AttendaceComposable
import com.example.attendance.composables.ConfirmationDialog
import com.example.attendance.composables.DrawerContent
import com.example.attendance.composables.FilterDropdown
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavController) {
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val storageManager = StorageManager(LocalContext.current)
    val isLogoutLoading by viewModel.isLogoutLoading.collectAsState()
    val userId = storageManager.getUserId()
    val token = storageManager.getToken()
    var showDialog by remember { mutableStateOf(false) }
    var dialogPurpose by remember { mutableStateOf("") }
    val hasUnchecked by viewModel.hasUnchecked.collectAsState()
    val isActionButtonLoading by viewModel.isActionButtonLoading.collectAsState()
    val isExtraLoading by viewModel.isExtraLoading.collectAsState()
    // Drawer state
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)


    val scope = rememberCoroutineScope()

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .debounce(300L)  // Debounce to avoid rapid requests
            .collect { visibleItems ->
                // Get the index of the last visible item
                val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: -1

                val threshold = 5
                // Check if the user scrolled near the end
                if (attendanceRecords.isNotEmpty() &&
                    lastVisibleItemIndex >= attendanceRecords.size - threshold) {

                    // Only fetch more data if the list size is divisible by 20 and not currently loading
                    if (attendanceRecords.size % 20 == 0 && !isLoading) {
                        viewModel.fetchAttendanceRecords(token!!, fullRefresh = false, userId)
                    }
                }
            }
    }
    LaunchedEffect(Unit) {
        viewModel.fetchAttendanceRecords(token!!, fullRefresh = true, userId)
    }
    // Show Toast if there is an error message
    val context = LocalContext.current
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(scope, drawerState, storageManager, navController, onLogout = {
                dialogPurpose = "logout"
                showDialog = true
            }, isLogoutLoading)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Home Screen") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            floatingActionButton = {
                // If no records the button will be refresh
                if (attendanceRecords.isEmpty()) {
                    FloatingActionButton(
                        onClick = {
                            // Don't let user press button is its already loading
                            if (!isLoading){
                                viewModel.fetchAttendanceRecords(token!!, fullRefresh = true, userId)
                            }
                        }
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator()
                        }else{
                            Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                        }
                    }
                } else{
                    FloatingActionButton(
                        onClick = {
                            // If its already not loading go ahead
                            if (! isActionButtonLoading){
                                if (hasUnchecked){
                                    dialogPurpose = "checkOut"
                                    showDialog = true
                                } else{
                                    dialogPurpose = "checkIn"
                                    showDialog = true
                                }
                            }

                        }
                    ) {
                        // If its already pressed show loading
                        if (isActionButtonLoading){
                            CircularProgressIndicator()
                        } else{
                            if (hasUnchecked){
                                Icon(Icons.AutoMirrored.Default.ExitToApp, contentDescription = "Check out")
                            }  else{
                                Icon(Icons.Default.Check, contentDescription = "Check in")
                            }
                        }

                    }
                }
            }
        ) { paddingValues ->
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    if (! isLoading){
                        viewModel.fetchAttendanceRecords(token = token!!, fullRefresh = true, userId)
                    } else{
                        Toast.makeText(context, "Please wait for the loading to finish", Toast.LENGTH_LONG).show()
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,

                ) {

                    Text("Attendance Records", style = MaterialTheme.typography.titleLarge)
                    FilterDropdown(onFilterSelected = { selected ->
                        viewModel.updateSelectedFilter(selected)
                        viewModel.fetchAttendanceRecords(token!!, fullRefresh = true, userId)
                    })
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        if (attendanceRecords.isEmpty()) {
                            LazyColumn {
                                items(1){
                                    Text("No records")
                                }
                            }
                        } else {
                            // Confirmation dialog
                            when (dialogPurpose) {
                                "logout" -> ConfirmationDialog(
                                    showDialog = showDialog,
                                    title = "Logout",
                                    message = "Are you sure you want to logout?",
                                    confirmButtonText = "Yes",
                                    dismissButtonText = "Cancel",
                                    onDismiss = { showDialog = false },
                                    onConfirm = {
                                        viewModel.logout(onSuccess = {
                                            Toast.makeText(context, "Logged out", Toast.LENGTH_LONG).show()
                                            storageManager.clearToken()
                                            storageManager.clearUserId()
                                            navController.navigate(NavRoutes.Login.route) {
                                                popUpTo(NavRoutes.Home.route) { inclusive = true }
                                            }
                                        }, token!!)
                                        showDialog = false
                                    }
                                )
                                "checkOut" -> ConfirmationDialog(
                                    showDialog = showDialog,
                                    title = "Check out",
                                    message = "Are you sure you want check out?",
                                    confirmButtonText = "Yes",
                                    dismissButtonText = "Cancel",
                                    onDismiss = { showDialog = false },
                                    onConfirm = {
                                        viewModel.checkOut(token!!, userId)
                                        showDialog = false
                                    }
                                )
                                "checkIn" -> ConfirmationDialog(
                                    showDialog = showDialog,
                                    title = "Check in",
                                    message = "Are you sure you want check in?",
                                    confirmButtonText = "Yes",
                                    dismissButtonText = "Cancel",
                                    onDismiss = { showDialog = false },
                                    onConfirm = {
                                        viewModel.checkIn(token!!, userId)
                                        showDialog = false
                                    }
                                )
                            }

                            LazyColumn(
                                Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                state = listState,
                                ) {
                                items(attendanceRecords) { record ->
                                    AttendaceComposable(record, userId)
                                }
                            }
                            if (isExtraLoading){
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}



