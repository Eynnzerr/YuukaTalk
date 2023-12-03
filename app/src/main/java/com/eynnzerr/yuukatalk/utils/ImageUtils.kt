package com.eynnzerr.yuukatalk.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.collection.LruCache
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

object ImageUtils {
    fun generateBitmap(view: View): Bitmap {
        if (view is RecyclerView) {
            val padding = 16
            val screenshot = Bitmap.createBitmap(view.width + 2 * padding, view.computeVerticalScrollRange() + 2 * padding, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(screenshot).apply { drawColor(Color.parseColor("#fff7e3")) }
            var iHeight = 0f

            view.adapter?.let { adapter ->
                val paint = Paint()
                val cacheSize = (Runtime.getRuntime().maxMemory() / 1024 / 4).toInt()
                val bitmapCache = LruCache<Int, Bitmap>(cacheSize)

                for (i in 0 until adapter.itemCount) {
                    val holder = adapter.createViewHolder(view, adapter.getItemViewType(i))
                    adapter.onBindViewHolder(holder, i)
                    holder.itemView.apply {
                        view.addView(this)
                        measure(
                            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                        )
                        layout(0, 0, holder.itemView.measuredWidth, holder.itemView.measuredHeight)
                        view.removeView(this)

                        val itemBitmap = Bitmap.createBitmap(
                            width,
                            height,
                            Bitmap.Config.ARGB_8888
                        )
                        val itemCanvas = Canvas(itemBitmap)
                        draw(itemCanvas)
                        bitmapCache.put(i, itemBitmap)
                    }
                }
                for (i in 0 until adapter.itemCount) {
                    val bitmap = bitmapCache[i]
                    canvas.drawBitmap(bitmap!!, 0F + padding, iHeight + padding, paint)
                    iHeight += bitmap.height
                    bitmap.recycle()
                }
            }

            return screenshot
        }

        // never used.
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }

    suspend fun saveBitMapToDisk(bitmap: Bitmap, context: Context): Uri {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "screenshot-${System.currentTimeMillis()}.png"
        )

        file.writeBitmap(bitmap, Bitmap.CompressFormat.PNG, 100)

        return scanFilePath(context, file.path) ?: throw Exception("File could not be saved")
    }

    private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
        }
    }

    private suspend fun scanFilePath(context: Context, filePath: String): Uri? {
        return suspendCancellableCoroutine { continuation ->
            MediaScannerConnection.scanFile(
                context,
                arrayOf(filePath),
                arrayOf("image/png")
            ) { _, scannedUri ->
                if (scannedUri == null) {
                    continuation.cancel(Exception("File $filePath could not be scanned"))
                } else {
                    continuation.resume(scannedUri)
                }
            }
        }
    }

    private const val TAG = "ImageUtils"
}