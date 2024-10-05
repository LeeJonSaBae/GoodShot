package com.ijonsabae.presentation.shot

import VideoEncoder
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer


object SwingVideoProcessor {
    const val GUEST_ID: Long = -1L
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/SwingAnalysis"
            )
        }

        var uri: Uri? = null
        try {
            uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            uri?.let {
                val outputStream: OutputStream? = context.contentResolver.openOutputStream(it)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                }
            }
        } catch (e: Exception) {
            Log.e("싸피", "Error saving bitmap: ${e.message}")
        }

        return uri
    }

    @SuppressLint("HardwareIds")
    fun saveSwingVideo(context: Context, bitmapIndices: List<Bitmap>, userId: Long = GUEST_ID) : String {
        val fileSaveTime = System.currentTimeMillis()
        val ssidName = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val videoFileName = "${ssidName}_${fileSaveTime}.mp4"
        val videoDir = File(context.filesDir, "videos/$userId")
        if (!videoDir.exists()) {
            videoDir.mkdirs()
        }
        val videoFile = File(videoDir, videoFileName)
        val videoEncoder = VideoEncoder(
            bitmapIndices[0].width,
            bitmapIndices[0].height,
            12,
            videoFile.absolutePath
        )
        videoEncoder.start()
        Log.d("MainActivity_Capture", "인덱스들: ${bitmapIndices.size}")
        bitmapIndices.forEachIndexed { index, bitmap ->
            val byteBuffer = bitmapToByteBuffer(bitmap)
            Log.d("MainActivity_Capture", "프레임 인덱스: $index")
            videoEncoder.encodeFrame(byteBuffer)
        }

        videoEncoder.finish()
        saveBitmapToInternalStorage(context, bitmapIndices[0], userId, ssidName, fileSaveTime)

        return videoFileName

    }

    private fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, userName: Long, ssidName: String, fileSaveTime: Long) {
        val thumbnailFileName = "${ssidName}_${fileSaveTime}.jpg"
        val thumbnailDir = File(context.filesDir, "thumbnails/$userName")
        if (!thumbnailDir.exists()) {
            thumbnailDir.mkdirs()
        }

        val file = File(thumbnailDir, thumbnailFileName)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        Log.d("MainActivity_Capture", "썸네일 저장 완료")
    }

    private fun bitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val inputWidth = bitmap.width
        val inputHeight = bitmap.height
        val yuvImage = ByteArray(inputWidth * inputHeight * 3 / 2)
        val argb = IntArray(inputWidth * inputHeight)

        bitmap.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight)

        encodeYUV420SP(yuvImage, argb, inputWidth, inputHeight)

        return ByteBuffer.wrap(yuvImage)
    }

    private fun encodeYUV420SP(yuv420sp: ByteArray, argb: IntArray, width: Int, height: Int) {
        val frameSize = width * height
        var yIndex = 0
        var uvIndex = frameSize

        for (j in 0 until height) {
            for (i in 0 until width) {
                val rgb = argb[j * width + i]
                val r = rgb shr 16 and 0xFF
                val g = rgb shr 8 and 0xFF
                val b = rgb and 0xFF
                val y = (66 * r + 129 * g + 25 * b + 128 shr 8) + 16
                yuv420sp[yIndex++] = y.toByte()

                if (j % 2 == 0 && i % 2 == 0) {
                    val u = (-38 * r - 74 * g + 112 * b + 128 shr 8) + 128
                    val v = (112 * r - 94 * g - 18 * b + 128 shr 8) + 128
                    yuv420sp[uvIndex++] = u.toByte()
                    yuv420sp[uvIndex++] = v.toByte()
                }
            }
        }
    }



}