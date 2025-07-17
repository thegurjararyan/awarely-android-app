package com.example.awarely.ui


import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.example.awarely.model.SessionEntity
import com.example.awarely.utils.ChartSharer

@Composable
fun ShareScreen(sessions: List<SessionEntity>) {
    val context = LocalContext.current
    var chartView by remember { mutableStateOf<PieChart?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Your App Usage", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        AndroidView(factory = { ctx ->
            PieChart(ctx).apply {
                description.isEnabled = false
                isDrawHoleEnabled = false
                setUsePercentValues(false)
                legend.isEnabled = true
                chartView = this
                data = generatePieData(sessions)
                invalidate()
            }
        }, modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                chartView?.let { ChartSharer.shareChart(it, context) }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📤 Share Usage Chart")
        }
    }
}

fun generatePieData(sessions: List<SessionEntity>): PieData {
    val usageMap = sessions.groupBy { it.appName }
        .mapValues { entry -> entry.value.sumOf { it.durationMinutes } }

    val entries = usageMap.map { PieEntry(it.value.toFloat(), it.key) }
    val dataSet = PieDataSet(entries, "Usage").apply {
        colors = ColorTemplate.MATERIAL_COLORS.toList()
        valueTextSize = 14f
    }

    return PieData(dataSet)
}
