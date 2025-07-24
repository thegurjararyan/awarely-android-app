// In app/src/main/java/com/example/awarely/utils/ChartSharer.kt
package com.example.awarely.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.github.mikephil.charting.charts.Chart
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ChartSharer {

    fun shareChart(chart: Chart<*>, context: Context) {
        val bitmap = chart.chartBitmap
        if (bitmap != null) {
            try {
                val cachePath = File(context.cacheDir, "images").apply { mkdirs() }
                val file = File(cachePath, "usage_chart.png")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                val uri: Uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "My App Usage Chart")
                    putExtra(Intent.EXTRA_TEXT, "Check out my app usage statistics!")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                context.startActivity(Intent.createChooser(shareIntent, "Share chart via"))
            } catch (e: IOException) {
                e.printStackTrace()
                // Optional: show user feedback
            }
        }
    }
}
