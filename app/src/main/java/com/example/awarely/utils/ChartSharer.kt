// In app/src/main/java/com/example/awarely/utils/ChartSharer.kt
package com.example.awarely.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.github.mikephil.charting.charts.PieChart
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ChartSharer {

    fun shareChart(chart: PieChart, context: Context) {
        // Logic to capture chart as an image and share it
        val bitmap = chart.getChartBitmap()
        if (bitmap != null) {
            try {
                val cachePath = File(context.cacheDir, "images")
                cachePath.mkdirs() // Create directory if not exists
                val stream = FileOutputStream("$cachePath/image.png") // Overwrites this image every time
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()

                val imageFile = File("$cachePath/image.png")
                val contentUri: Uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider", // Make sure this matches your FileProvider authority
                    imageFile
                )

                if (contentUri != null) {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Temp permission for receiving app to read image
                        setDataAndType(contentUri, context.contentResolver.getType(contentUri))
                        putExtra(Intent.EXTRA_STREAM, contentUri)
                        putExtra(Intent.EXTRA_SUBJECT, "My App Usage Chart")
                        putExtra(Intent.EXTRA_TEXT, "Check out my app usage statistics!")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share chart via"))
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle error, e.g., show a Toast message
            }
        } else {
            // Handle error: bitmap is null
        }
    }
}
    