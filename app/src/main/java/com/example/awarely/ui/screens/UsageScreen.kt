package com.example.awarely.ui

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.awarely.viewmodel.UsageViewModel
import com.example.awarely.model.UsageData
import androidx.lifecycle.ViewModelProvider
import com.example.awarely.viewmodel.UsageViewModelFactory

@Composable
fun UsageScreen(app: Application) {
    val viewModel: UsageViewModel = viewModel(factory = UsageViewModelFactory(app))
    var usageData by remember { mutableStateOf<List<UsageData>>(emptyList()) }
    var selectedDays by remember { mutableStateOf(1) }

    LaunchedEffect(selectedDays) {
        usageData = viewModel.getUsageDataForDays(selectedDays)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Days selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { selectedDays = 1 }) { Text("Today") }
            Button(onClick = { selectedDays = 7 }) { Text("Last 7 Days") }
            Button(onClick = { selectedDays = 30 }) { Text("Last 30 Days") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (usageData.isEmpty()) {
            Text("No usage data available", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(usageData) { item ->
                    UsageItem(item)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Built with ❤️ by Aryan Chaudhary",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun UsageItem(data: UsageData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = data.appName, style = MaterialTheme.typography.titleMedium)
            Text(text = data.usageTimeFormatted, style = MaterialTheme.typography.bodySmall)
        }
    }
}
