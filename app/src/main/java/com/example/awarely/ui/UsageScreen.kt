package com.example.awarely.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.awarely.model.UsageData
import com.example.awarely.ui.components.UsageBarChart
import com.example.awarely.viewmodel.UsageViewModel
import kotlinx.coroutines.launch

@Composable
fun UsageScreen(
    viewModel: UsageViewModel,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var selectedRange by remember { mutableStateOf(1) } // 1 = Today
    val usageData by produceState<List<UsageData>>(initialValue = emptyList(), selectedRange) {
        scope.launch {
            value = viewModel.getUsageDataForDays(selectedRange)
        }
    }

    val rangeOptions = listOf("Today", "7 Days", "30 Days")
    val rangeValues = listOf(1, 7, 30)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "App Usage",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rangeOptions.forEachIndexed { index, label ->
                    val isSelected = selectedRange == rangeValues[index]
                    OutlinedButton(
                        onClick = { selectedRange = rangeValues[index] },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else Color.Transparent
                        )
                    ) {
                        Text(text = label)
                    }
                }
            }

            UsageBarChart(
                data = usageData,  // Changed from 'usageData' to 'data'
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }

        Text(
            text = "Built with ❤️ by Aryan Chaudhary",
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp, bottom = 4.dp),
            color = Color.Gray
        )
    }
}