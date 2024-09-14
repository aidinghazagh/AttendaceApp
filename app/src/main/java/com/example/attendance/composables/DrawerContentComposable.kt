package com.example.attendance.composables


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendance.StorageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(scope: CoroutineScope, drawerState: DrawerState, storageManager: StorageManager, navController: NavController, onLogout: () -> Unit, isLogoutLoading: Boolean) {
    ModalDrawerSheet {
        Text("Home Screen", modifier = Modifier.padding(16.dp))
        HorizontalDivider()
        if (isLogoutLoading){
            CircularProgressIndicator()
        } else{
            NavigationDrawerItem(
                label = { Text(text = "Logout") },
                selected = false,
                onClick = {
                    scope.launch {
                        drawerState.close() // Close the drawer
                        onLogout() // Call the logout function
                    }
                }
            )
        }

        // ...other drawer items
    }
}