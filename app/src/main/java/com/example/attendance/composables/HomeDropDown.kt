package com.example.attendance.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun FilterDropdown(onFilterSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("All", "Mine")
    var selectedItem by remember { mutableStateOf(items[0]) }


    Box(modifier = Modifier.fillMaxWidth()) {
        // Button to toggle dropdown
        OutlinedButton (
            onClick = { expanded = true }, // Set expanded to true when clicked
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedItem) // Show selected item
        }

        // Dropdown menu that opens when expanded is true
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false } // Close dropdown when clicked outside
        ) {
            items.forEach { item ->
                DropdownMenuItem(onClick = {
                    selectedItem = item // Update selected item
                    expanded = false // Collapse dropdown
                    onFilterSelected(item) // Notify parent about the selected item
                }, text = { Text(item) })
            }
        }
    }
}
