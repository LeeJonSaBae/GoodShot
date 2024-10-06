package com.ijonsabae.presentation.shot

import VideoEncoder
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
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
    fun saveSwingDataToInternalStorage(context: Context, swingPoseBitmap: List<Bitmap>, swingImages: List<Bitmap>, isSelf: Boolean, userId: Long = GUEST_ID) : Pair<String, Long> {
        var bitmaps = swingImages
        var swingPoses = swingPoseBitmap
        if (isSelf) {
            bitmaps = flipBitmapsHorizontally(swingImages)
            swingPoses = flipBitmapsHorizontally(swingPoseBitmap)
        }
        val fileSaveTime = System.currentTimeMillis()
        val ssidName = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val swingCode = "${ssidName}_${fileSaveTime}"

        //스윙 영상 저장
        saveSwingVideo(context, bitmaps, swingCode, userId)

        //썸네일 저장
        saveThumbnailToInternalStorage(context, bitmaps[0], swingCode, userId)

        //각 스윙별 이미지 저장
        saveSwingPoseImagesToInternalStorage(context, swingPoses , swingCode, userId)

        return Pair(swingCode, fileSaveTime)
    }

    @SuppressLint("HardwareIds")
    private fun saveSwingVideo(context: Context, bitmaps: List<Bitmap>, swingCode: String, userId: Long) {
        val videoFileName = "$swingCode.mp4"
        val videoDir = File(context.filesDir, "videos/$userId")
        if (!videoDir.exists()) {
            videoDir.mkdirs()
        }
        val videoFile = File(videoDir, videoFileName)
        val videoEncoder = VideoEncoder(
            bitmaps[0].width,
            bitmaps[0].height,
            12,
            videoFile.absolutePath
        )
        videoEncoder.start()
        bitmaps.forEachIndexed { index, bitmap ->
            val byteBuffer = bitmapToByteBuffer(bitmap)
            videoEncoder.encodeFrame(byteBuffer)
        }

        videoEncoder.finish()
    }

    private fun saveThumbnailToInternalStorage(context: Context, bitmap: Bitmap, swingCode: String, userId: Long) {
        val thumbnailFileName = "${swingCode}.jpg"
        val thumbnailDir = File(context.filesDir, "thumbnails/$userId")
        if (!thumbnailDir.exists()) {
            thumbnailDir.mkdirs()
        }

        val file = File(thumbnailDir, thumbnailFileName)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        Log.d("MainActivity_Capture", "썸네일 저장 완료")
    }



    private fun saveSwingPoseImagesToInternalStorage(context: Context, bitmaps: List<Bitmap>, swingCode: String, userId: Long) {
        val swingPoseDir = File(context.filesDir, "swingPose/$userId")
        if (!swingPoseDir.exists()) {
            swingPoseDir.mkdirs()
        }
        bitmaps.forEachIndexed { poseIndex, bitmap ->

            val swingPoseFileName = "${swingCode}_${poseIndex}.jpg"
            val file = File(swingPoseDir, swingPoseFileName)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        }
    }
    // TODO 문현 : 영상 삭제 로직 만들기(룸에서는 지우는거 구현 완료, 로컬 기기 삭제 필요)

    // TODO 영민 : File객체로 경로를 반환하는 함수입니다.
    fun getSwingVideoFile(context: Context, swingCode: String, userId: Long = GUEST_ID): File {
        val videoFileName = "$swingCode.mp4"
        val videoDir = File(context.filesDir, "videos/$userId")
        return File(videoDir, videoFileName)
    }

    fun getSwingThumbnailFile(context: Context, swingCode: String, userId: Long = GUEST_ID): File {
        val thumbnailFileName = "$swingCode.jpg"
        val thumbnailDir = File(context.filesDir, "thumbnails/$userId")
        return File(thumbnailDir, thumbnailFileName)
    }

    fun getSwingPoseFiles(context: Context, swingCode: String, userId: Long = GUEST_ID): List<File> {
        val swingPoseFiles: MutableList<File> = mutableListOf()
        val swingPoseDir = File(context.filesDir, "swingPose/$userId")
        for (poseIndex in 0..7) {
            val swingPoseFileName = "${swingCode}_${poseIndex}.jpg"
            swingPoseFiles.add(File(swingPoseDir, swingPoseFileName))
        }
        return swingPoseFiles
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

    fun flipBitmapHorizontally(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply { postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    fun flipBitmapsHorizontally(bitmaps: List<Bitmap>): List<Bitmap> {
        return bitmaps.map { bitmap ->
            val matrix = Matrix().apply { postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
    }




}