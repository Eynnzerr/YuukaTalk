package com.eynnzerr.yuukatalk.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import coil.executeBlocking
import coil.imageLoader
import coil.request.ImageRequest
import com.eynnzerr.yuukatalk.base.YuukaTalkApplication
import com.eynnzerr.yuukatalk.data.model.Talk
import com.eynnzerr.yuukatalk.data.preference.PreferenceKeys
import com.eynnzerr.yuukatalk.ui.view.TalkAdapter
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.coroutines.resume
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt

object ImageUtils {

    enum class ExportStage {
        PRELOAD,
        MEASURE,
        DRAW
    }

    data class ExportProgress(
        val stage: ExportStage,
        val current: Int,
        val total: Int,
    )

    private val mmkv = MMKV.defaultMMKV()

    val defaultExportPath: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath

    fun generateBitmap(view: View): Bitmap = runBlocking {
        generateBitmapSuspend(view, yieldEveryItems = Int.MAX_VALUE)
    }

    suspend fun generateBitmapSuspend(
        view: View,
        yieldEveryItems: Int = 1,
        onProgress: ((ExportProgress) -> Unit)? = null,
    ): Bitmap {
        requireMainThread()

        if (view is RecyclerView) {
            val adapter = view.adapter
            return if (adapter != null) {
                generateBitmapInRangeSuspend(view, 0..<adapter.itemCount, yieldEveryItems, onProgress)
            } else {
                createBitmap(view.width.coerceAtLeast(1), view.height.coerceAtLeast(1))
            }
        }

        // Normally won't be reachable.
        val bitmap = createBitmap(view.width.coerceAtLeast(1), view.height.coerceAtLeast(1))
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }

    // legacy API, keep for compatibility
    fun generateBitMapInRange(view: RecyclerView, range: IntRange): Bitmap = runBlocking {
        generateBitmapInRangeSuspend(view, range, yieldEveryItems = Int.MAX_VALUE)
    }

    suspend fun generateBitmapInRangeSuspend(
        view: RecyclerView,
        range: IntRange,
        yieldEveryItems: Int = 1,
        onProgress: ((ExportProgress) -> Unit)? = null,
    ): Bitmap {
        requireMainThread()
        val adapter = view.adapter
            ?: return createBitmap(view.width.coerceAtLeast(1), view.height.coerceAtLeast(1))

        ensureValidRange(adapter.itemCount, range)
        if (range.isEmpty()) {
            return createBitmap(view.width.coerceAtLeast(1), view.height.coerceAtLeast(1))
        }

        val totalCount = range.count()
        preloadPhotosIfNeeded(adapter, range, yieldEveryItems, onProgress)

        val viewHolderAdapter = adapter.asViewHolderAdapter()
        val itemHeights = IntArray(totalCount)
        var totalHeight = 0

        for ((indexInRange, position) in range.withIndex()) {
            val itemView = createAndLayoutItemView(view, viewHolderAdapter, position)
            val itemHeight = itemView.measuredHeight.coerceAtLeast(1)
            itemHeights[indexInRange] = itemHeight
            totalHeight += itemHeight
            reportProgress(onProgress, ExportStage.MEASURE, indexInRange + 1, totalCount)
            maybeYield(indexInRange, yieldEveryItems)
        }

        val padding = 16
        val screenshot = createBitmap(
            (view.width + 2 * padding).coerceAtLeast(1),
            (totalHeight + 2 * padding).coerceAtLeast(1)
        )
        try {
            val canvas = Canvas(screenshot)
            canvas.drawColor(resolveBackgroundColor())

            var yOffset = padding.toFloat()
            for ((indexInRange, position) in range.withIndex()) {
                val itemView = createAndLayoutItemView(view, viewHolderAdapter, position)
                canvas.save()
                canvas.translate(padding.toFloat(), yOffset)
                itemView.draw(canvas)
                canvas.restore()
                yOffset += itemHeights[indexInRange]
                reportProgress(onProgress, ExportStage.DRAW, indexInRange + 1, totalCount)
                maybeYield(indexInRange, yieldEveryItems)
            }

            drawWatermark(canvas)
            return screenshot
        } catch (e: CancellationException) {
            if (!screenshot.isRecycled) {
                screenshot.recycle()
            }
            throw e
        }
    }

    // 用户一定已经授予了读写权限。首先尝试直接写入设置的指定输出目录。如果失败，写入到私有目录中。
    // 无论哪条路径，最终都返回可分享的 content:// uri。
    suspend fun saveBitMapToDisk(bitmap: Bitmap, context: Context): Uri {
        val formatIndex = mmkv.decodeInt(PreferenceKeys.COMPRESS_FORMAT, 1)
        val format = getCompressFormat(formatIndex)
        val suffix = compressSuffix(formatIndex)
        val quality = mmkv.decodeInt(PreferenceKeys.IMAGE_QUALITY, 100)
        val fileName = "screenshot-${System.currentTimeMillis()}.$suffix"

        Log.d(TAG, "saveBitMapToDisk: format=${format.name}, suffix=$suffix, quality=$quality")

        val savePath = mmkv.decodeString(PreferenceKeys.IMAGE_EXPORT_PATH) ?: defaultExportPath
        val targetDir = File(savePath)

        runCatching {
            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }
            val file = File(targetDir, fileName)
            file.writeBitmap(bitmap, format, quality)
            scanFilePath(context, file.path) ?: toFileProviderUri(context, file)
        }.onSuccess { uri ->
            Log.d(TAG, "saveBitMapToDisk: main-path uri=$uri")
            return uri
        }.onFailure {
            it.printStackTrace()
            Log.d(TAG, "saveBitMapToDisk: main-path failed, fallback to private dir.")
        }

        val privateFile = File(PathUtils.getImageFallbackExportDir(), fileName).apply {
            writeBitmap(bitmap, format, quality)
        }

        copyImageToGallery(context, privateFile, suffix)?.let { uri ->
            Log.d(TAG, "saveBitMapToDisk: mediastore-fallback uri=$uri")
            return uri
        }

        val fallbackUri = toFileProviderUri(context, privateFile)
        Log.d(TAG, "saveBitMapToDisk: fileprovider-fallback uri=$fallbackUri")
        return fallbackUri
    }

    fun isImageBase64(imageUri: String) = !imageUri.startsWith("file://") && !imageUri.startsWith("content://") && !imageUri.startsWith("/storage")

    suspend fun convertImageToBase64(imageUri: String): String? = withContext(Dispatchers.IO) {
        try {
            val contentResolver = YuukaTalkApplication.context.contentResolver
            contentResolver.openInputStream(Uri.parse(imageUri))?.use { imageInputStream ->
                val outputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var length: Int
                while (imageInputStream.read(buffer).also { length = it } != -1) {
                    outputStream.write(buffer, 0, length)
                }
                Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "convertImageToBase64: Failed to convert image to Base64. Fallback to use primary uri. This usually happens with bundled asset images.")
            imageUri
        }
    }

    internal fun compressSuffix(formatIndex: Int): String {
        return when (formatIndex) {
            0 -> "jpeg"
            1 -> "png"
            else -> "webp"
        }
    }

    internal fun ensureValidRange(itemCount: Int, range: IntRange) {
        if (range.isEmpty()) return
        require(range.first >= 0 && range.last < itemCount) {
            "Range outbounds adapter count. itemCount=$itemCount, range=$range"
        }
    }

    private suspend fun preloadPhotosIfNeeded(
        adapter: RecyclerView.Adapter<*>,
        range: IntRange,
        yieldEveryItems: Int,
        onProgress: ((ExportProgress) -> Unit)? = null,
    ) {
        val talkAdapter = adapter as? TalkAdapter ?: return
        if (range.isEmpty()) return

        // Snapshot data on main thread, then preload/decode off main thread to avoid ANR.
        val talks = range.map { index -> talkAdapter.getData()[index] }
        reportProgress(onProgress, ExportStage.PRELOAD, 0, talks.size)
        withContext(Dispatchers.IO) {
            talks.forEachIndexed { indexInRange, talk ->
                if (talk is Talk.Photo) {
                    Log.d(TAG, "generateBitmapInRangeSuspend: preloading photo ${range.first + indexInRange}")
                    val photoUri = talk.uri
                    val data = if (isImageBase64(photoUri)) Base64.decode(photoUri, Base64.DEFAULT) else photoUri
                    val request = ImageRequest.Builder(YuukaTalkApplication.context)
                        .data(data)
                        .build()
                    YuukaTalkApplication.context.imageLoader.executeBlocking(request)
                }
                reportProgress(onProgress, ExportStage.PRELOAD, indexInRange + 1, talks.size)
                if (yieldEveryItems <= 0 || yieldEveryItems == Int.MAX_VALUE) return@forEachIndexed
                if ((indexInRange + 1) % yieldEveryItems == 0) {
                    yield()
                }
            }
        }
    }

    private fun resolveBackgroundColor(): Int {
        val rawColor = mmkv.decodeString(PreferenceKeys.BACKGROUND_COLOR) ?: "#fff7e3"
        return runCatching { rawColor.toColorInt() }
            .getOrDefault("#fff7e3".toColorInt())
    }

    private fun drawWatermark(canvas: Canvas) {
        if (!mmkv.decodeBool(PreferenceKeys.USE_WATERMARK, false)) return

        val author = mmkv.decodeString(PreferenceKeys.AUTHOR_NAME, "")
        val paint = Paint().apply {
            textSize = 20f
            color = Color.DKGRAY
        }
        canvas.drawText(
            "Author:$author",
            0f,
            (canvas.height - 25).toFloat(),
            paint
        )
        canvas.drawText(
            "Made by YuukaTalk ${VersionUtils.getLocalVersion()}",
            0f,
            (canvas.height - 5).toFloat(),
            paint
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun RecyclerView.Adapter<*>.asViewHolderAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder> {
        return this as RecyclerView.Adapter<RecyclerView.ViewHolder>
    }

    private fun createAndLayoutItemView(
        parent: RecyclerView,
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        position: Int
    ): View {
        val holder = adapter.createViewHolder(parent, adapter.getItemViewType(position))
        adapter.onBindViewHolder(holder, position)

        return holder.itemView.apply {
            parent.addView(this)
            measure(
                View.MeasureSpec.makeMeasureSpec(parent.width.coerceAtLeast(1), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            layout(0, 0, measuredWidth, measuredHeight)
            parent.removeView(this)
        }
    }

    private suspend fun maybeYield(indexInRange: Int, yieldEveryItems: Int) {
        if (yieldEveryItems <= 0 || yieldEveryItems == Int.MAX_VALUE) return
        if ((indexInRange + 1) % yieldEveryItems == 0) {
            yield()
        }
    }

    private suspend fun reportProgress(
        callback: ((ExportProgress) -> Unit)?,
        stage: ExportStage,
        current: Int,
        total: Int,
    ) {
        if (callback == null) return
        if (total > 0) {
            val normalizedCurrent = current.coerceAtLeast(0)
            val step = (total / 100).coerceAtLeast(1)
            if (normalizedCurrent != 0 && normalizedCurrent != total && normalizedCurrent % step != 0) {
                return
            }
        }
        val update = ExportProgress(
            stage = stage,
            current = current.coerceAtLeast(0),
            total = total.coerceAtLeast(0)
        )
        if (Looper.myLooper() == Looper.getMainLooper()) {
            callback(update)
        } else {
            withContext(Dispatchers.Main.immediate) {
                callback(update)
            }
        }
    }

    private fun copyImageToGallery(context: Context, imageFile: File, suffix: String = "png"): Uri? {
        return runCatching {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.name)
                put(MediaStore.Images.Media.MIME_TYPE, mimeTypeForSuffix(suffix))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val resolver: ContentResolver = context.contentResolver
            val collectionUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val itemUri: Uri = resolver.insert(collectionUri, values)
                ?: return@runCatching null

            resolver.openOutputStream(itemUri)?.use { outputStream ->
                imageFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: return@runCatching null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(itemUri, values, null, null)
            }

            itemUri
        }.getOrElse {
            it.printStackTrace()
            null
        }
    }

    private fun toFileProviderUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.file_provider",
            file
        )
    }

    private fun mimeTypeForSuffix(suffix: String): String {
        return when (suffix.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "webp" -> "image/webp"
            else -> "image/png"
        }
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
                arrayOf("image/*")
            ) { _, scannedUri ->
                if (continuation.isActive) {
                    continuation.resume(scannedUri)
                }
            }
        }
    }

    private fun requireMainThread() {
        require(Looper.myLooper() == Looper.getMainLooper()) {
            "Image export rendering must run on main thread."
        }
    }

    private val compressFormatMap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        mapOf(
            0 to Bitmap.CompressFormat.JPEG,
            1 to Bitmap.CompressFormat.PNG,
            2 to Bitmap.CompressFormat.WEBP_LOSSY,
            3 to Bitmap.CompressFormat.WEBP_LOSSLESS
        )
    } else {
        mapOf(
            0 to Bitmap.CompressFormat.JPEG,
            1 to Bitmap.CompressFormat.PNG,
        )
    }

    private fun getCompressFormat(index: Int) = compressFormatMap.getOrDefault(index, Bitmap.CompressFormat.PNG)

    private const val TAG = "ImageUtils"
}
