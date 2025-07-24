package com.example.awarely

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.awarely.ui.UsageScreen
import com.example.awarely.ui.theme.AwarelyTheme
import com.example.awarely.viewmodel.UsageViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AwarelyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val usageViewModel: UsageViewModel = viewModel()
                    UsageScreen(
                        viewModel = usageViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}