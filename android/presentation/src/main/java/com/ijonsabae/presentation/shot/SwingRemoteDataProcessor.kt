package com.ijonsabae.presentation.shot

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ijonsabae.domain.model.SwingCommentExportImportParam
import com.ijonsabae.domain.model.SwingComparisonParam
import com.ijonsabae.domain.model.SwingFeedback
import com.ijonsabae.domain.model.SwingFeedbackComment
import com.ijonsabae.domain.model.SwingFeedbackExportImportParam
import com.ijonsabae.domain.usecase.login.GetUserIdUseCase
import com.ijonsabae.domain.usecase.profile.UploadPresignedDataUseCase
import com.ijonsabae.domain.usecase.replay.ExportSwingFeedbackListUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalChangedSwingFeedbackListUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackCommentUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackDataNeedSyncUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackListNeedToUploadUseCase
import com.ijonsabae.domain.usecase.replay.GetLocalSwingFeedbackListUseCase
import com.ijonsabae.domain.usecase.replay.GetRemoteSwingFeedbackListNeedToUploadUseCase
import com.ijonsabae.domain.usecase.replay.ImportSwingFeedbackListUseCase
import com.ijonsabae.domain.usecase.replay.SyncUpdateStatusUseCase
import com.ijonsabae.domain.usecase.shot.InsertLocalSwingFeedbackCommentUseCase
import com.ijonsabae.domain.usecase.shot.InsertLocalSwingFeedbackUseCase
import com.ijonsabae.presentation.config.Const.Companion.BACKSWING
import com.ijonsabae.presentation.config.Const.Companion.BAD
import com.ijonsabae.presentation.config.Const.Companion.DOWNSWING
import com.ijonsabae.presentation.config.Const.Companion.NICE
import com.ijonsabae.presentation.mapper.SwingFeedbackSyncRoomDataMapper
import com.ijonsabae.presentation.replay.IMAGE
import com.ijonsabae.presentation.replay.S3_URL
import com.ijonsabae.presentation.replay.THUMBNAIL
import com.ijonsabae.presentation.replay.VIDEO
import com.ijonsabae.presentation.util.formatTDateFromLongKorea
import com.ijonsabae.presentation.util.stringToTimeInMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.random.Random

private const val TAG = "SwingRemoteDataProcesso 싸피"

class SwingRemoteDataProcessor @Inject constructor(
    private val getUserIdUseCase: GetUserIdUseCase,
    private val  getLocalSwingFeedbackCommentUseCase: GetLocalSwingFeedbackCommentUseCase,
    private val  getChangedSwingFeedbackUseCase: GetLocalChangedSwingFeedbackListUseCase,
    private val  getSwingFeedbackDataNeedSyncUseCase: GetLocalSwingFeedbackDataNeedSyncUseCase,
    private val  getLocalSwingFeedbackListNeedToUploadUseCase: GetLocalSwingFeedbackListNeedToUploadUseCase,
    private val  syncUpdateStatusUseCase: SyncUpdateStatusUseCase,
    private val  getRemoteSwingFeedbackListNeedToUploadUseCase: GetRemoteSwingFeedbackListNeedToUploadUseCase,
    private val  uploadPresignedDataUseCase: UploadPresignedDataUseCase,
    private val  exportSwingFeedbackListUseCase: ExportSwingFeedbackListUseCase,
    private val  importSwingFeedbackListUseCase: ImportSwingFeedbackListUseCase,
    private val  getLocalSwingFeedbackListUseCase: GetLocalSwingFeedbackListUseCase,
    private val  insertLocalSwingFeedbackUseCase: InsertLocalSwingFeedbackUseCase,
    private val  insertLocalSwingFeedbackCommentUseCase: InsertLocalSwingFeedbackCommentUseCase
) {
    private fun sendProgressIntent(context: Context, progress: Int){
        val intent = Intent().apply {
            action = "progress_sync"
            putExtra("progress", progress)
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
    suspend fun uploadLocalSwingData(context: Context) {
        val userID = getUserIdUseCase()
        Log.d(TAG, "showCustomPopup: $userID")
        if (userID == -1L) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "비회원은 동기화를 진행하실 수 없습니다!", Toast.LENGTH_SHORT).show()
            }
        } else {
            // 바꿔야 할 SwingFeedback 목록 추출
            var progressStatus: Int = 0
            val changedSwingFeedbackList =
                SwingFeedbackSyncRoomDataMapper.mapperToSwingFeedbackSyncList(
                    getChangedSwingFeedbackUseCase(userID)
                )
            val progress1 = Random.nextInt(4, 7) / 2
            progressStatus += progress1
            sendProgressIntent(context, progress1)

            // 서버에 업데이트 보냄
            getSwingFeedbackDataNeedSyncUseCase(changedSwingFeedbackList).getOrThrow()
            val progress2 = Random.nextInt(6, 12) / 2
            progressStatus += progress2
            sendProgressIntent(context, progress2)

            // 이후 업데이트 보냈으니까 삭제된 것 제외 전부 업데이트 0으로 해서 업데이트 필요 없는 값으로 설정
            syncUpdateStatusUseCase(userID)
            val progress3 = Random.nextInt(15, 25) / 2
            progressStatus += progress3
            sendProgressIntent(context, progress3)

            // 0인 목록들을 뽑아서 Upload 후보군을 생성
            val list = getLocalSwingFeedbackListNeedToUploadUseCase(userID)
            val progress4 = Random.nextInt(8, 15) / 2
            progressStatus += progress4
            sendProgressIntent(context, progress4)

            // 업로드 후보군으로부터 진짜 업로드 대상을 추출
            val result =
                getRemoteSwingFeedbackListNeedToUploadUseCase(SwingComparisonParam(list.map { it.swingCode })).getOrThrow()
            val progress5 = Random.nextInt(8, 15) / 2
            progressStatus += progress5
            sendProgressIntent(context, progress5)

            // 진짜 업로드 대상에 대해서 반복적으로 각 대상마다 10개의 영상, 이미지를 업로드
            result.data.forEach { data ->
                val poseList =
                    SwingLocalDataProcessor.getSwingPoseFiles(context, data.code, userID)
                data.presignedUrls.forEachIndexed { index, url ->
                    when (index) {
                        0 -> {
                            uploadPresignedDataUseCase(
                                url,
                                SwingLocalDataProcessor.getSwingVideoFile(
                                    context,
                                    data.code,
                                    userID
                                ).toURI()
                            ).getOrThrow()
                        }

                        1 -> {
                            uploadPresignedDataUseCase(
                                url,
                                SwingLocalDataProcessor.getSwingThumbnailFile(
                                    context,
                                    data.code,
                                    userID
                                ).toURI()
                            ).getOrThrow()
                        }

                        else -> {
                            uploadPresignedDataUseCase(
                                url,
                                poseList[index - 2].toURI()
                            ).getOrThrow()
                        }
                    }
                }
                progressStatus += 15 / result.data.size / 2
                sendProgressIntent(context, 15 / result.data.size / 2)
            }

            // 업로드 대상에 대해서 돌아가면서 해당 업로드한 코드의 comment와 feedback을 가져와서 SwingExportParam 형성 및 Upload
            val codeSet = result.data.map { it.code }.toHashSet()

            exportSwingFeedbackListUseCase(
                list.filter {
                    it.swingCode in codeSet
                }.map { it ->
                    val data =
                        SwingFeedbackExportImportParam(
                            score = it.score,
                            title = it.title,
                            similarity = it.similarity,
                            likeStatus = it.likeStatus,
                            tempo = it.tempo,
                            solution = it.solution,
                            id = userID,
                            code = it.swingCode,
                            time = formatTDateFromLongKorea(it.date),
                            backSwingComments = getLocalSwingFeedbackCommentUseCase(
                                userID,
                                it.swingCode
                            ).filter { comment ->
                                comment.poseType == BACKSWING
                            }.map {
                                SwingCommentExportImportParam(
                                    commentType = if (it.commentType == NICE) {
                                        "NICE"
                                    } else {
                                        "BAD"
                                    },
                                    content = it.content
                                )
                            },
                            downSwingComments = getLocalSwingFeedbackCommentUseCase(
                                userID,
                                it.swingCode
                            ).filter {
                                it.poseType == DOWNSWING
                            }.map {
                                SwingCommentExportImportParam(
                                    commentType = if (it.commentType == NICE) {
                                        "NICE"
                                    } else {
                                        "BAD"
                                    },
                                    content = it.content
                                )
                            }
                        )
                    val progress6 = Random.nextInt(5, 7) / 2
                    progressStatus += progress6
                    sendProgressIntent(context, progress6)
                    data
                }
            ).getOrThrow()
            sendProgressIntent(context, (50 - progressStatus))
        }
    }
    suspend fun downloadRemoteSwingData(context: Context){
        var progressStatus = 0F
        val userID = getUserIdUseCase()

        val progress1 = Random.nextInt(3, 6)/2
        progressStatus += progress1
        sendProgressIntent(context, progress1)

        val list = getLocalSwingFeedbackListUseCase(userID)

        val progress2 = Random.nextInt(3, 6)/2
        progressStatus += progress2
        sendProgressIntent(context, progress2)

        val swingComparisonParamList = SwingComparisonParam(list.map { it.swingCode})
        val result = importSwingFeedbackListUseCase(swingComparisonParamList).getOrThrow()

        val progress3 = Random.nextInt(5 ,8)/2
        progressStatus += progress3
        sendProgressIntent(context, progress3)

        val progressRatio = (50 - progressStatus)/result.data.size
        withContext(Dispatchers.IO){
            val job = result.data.chunked(5).map { chunk ->
                launch {
                    chunk.forEach { swingFeedbackParam ->
                        val videoUri = Uri.parse(S3_URL + userID + VIDEO + swingFeedbackParam.code + ".mp4") // [파일 다운로드 주소 : 확장자명 포함되어야함]
                        val thumbnailUri = Uri.parse(S3_URL + userID + THUMBNAIL + swingFeedbackParam.code + ".jpg") // [파일 다운로드 주소 : 확장자명 포함되어야함]
                        Log.d(TAG, "showCustomPopup: $videoUri")
                        fun returnImageUri(idx: Int): Uri {
                            return Uri.parse(S3_URL + userID + IMAGE + swingFeedbackParam.code + "_" + idx + ".jpg") // [파일 다운로드 주소 : 확장자명 포함되어야함]
                        }

                        Log.d(TAG, "showCustomPopup: ${SwingLocalDataProcessor.getSwingVideoFile(context, userId = userID, swingCode = swingFeedbackParam.code).path}")
                        withContext(Dispatchers.IO) {
                            val jobVideo = launch {
                                downloadAndSaveFile(videoUri.toString(),  SwingLocalDataProcessor.getSwingVideoFile(context, userId = userID, swingCode = swingFeedbackParam.code).toString())
                                val progress6 = (progressRatio * Random.nextDouble(0.15, 0.25)).toInt()
                                progressStatus += progress6
                                sendProgressIntent(context, progress6)
                            }
                            val jobThumbnail = launch {
                                downloadAndSaveFile(thumbnailUri.toString(), SwingLocalDataProcessor.getSwingThumbnailFile(context, userId = userID, swingCode = swingFeedbackParam.code).toString())
                                val progress6 = (progressRatio * Random.nextDouble(0.05, 0.15)).toInt()
                                progressStatus += progress6
                                sendProgressIntent(context, progress6)
                            }
                            val jobSwingPose = launch {
                                val poseDestination = SwingLocalDataProcessor.getSwingPoseFiles(context, userId = userID, swingCode = swingFeedbackParam.code)

                                for(i in 0 until 8){
                                    val imageUrl = returnImageUri(i)
                                    launch {
                                        downloadAndSaveFile(imageUrl.toString(), poseDestination[i].toPath().toString())
                                    }
                                }
                            }
                            jobVideo.join()
                            jobThumbnail.join()
                            jobSwingPose.join()
                        }
                        val progress6 = (progressRatio * Random.nextDouble(0.5, 0.7)).toInt()
                        progressStatus += progress6
                        sendProgressIntent(context, progress6)

                        insertLocalSwingFeedbackUseCase(
                            SwingFeedback(
                                userID = userID,
                                title = swingFeedbackParam.title,
                                swingCode = swingFeedbackParam.code,
                                tempo = swingFeedbackParam.tempo,
                                date = stringToTimeInMillis(swingFeedbackParam.time),
                                score = swingFeedbackParam.score,
                                likeStatus = swingFeedbackParam.likeStatus,
                                similarity = swingFeedbackParam.similarity,
                                solution = swingFeedbackParam.solution
                            )
                        )

                        swingFeedbackParam.downSwingComments.forEach { comment ->
                            insertLocalSwingFeedbackCommentUseCase(
                                SwingFeedbackComment(
                                    userID = userID,
                                    swingCode = swingFeedbackParam.code,
                                    poseType = DOWNSWING,
                                    content = comment.content,
                                    commentType = if(comment.commentType == "NICE"){NICE}else{
                                        BAD
                                    }
                                )
                            )
                        }



                        swingFeedbackParam.backSwingComments.forEach { comment ->
                            insertLocalSwingFeedbackCommentUseCase(
                                SwingFeedbackComment(
                                    userID = userID,
                                    swingCode = swingFeedbackParam.code,
                                    poseType = BACKSWING,
                                    content = comment.content,
                                    commentType = if(comment.commentType == "NICE"){NICE}else{
                                        BAD
                                    }
                                )
                            )
                        }

                        val progress5 = (progressRatio * Random.nextDouble(0.2, 0.3)).toInt()
                        progressStatus += progress5
                        sendProgressIntent(context, progress5)
                    }
                }
            }
            job.joinAll()
        }


        sendProgressIntent(context, (50 - progressStatus.toInt()))
    }

    private suspend fun downloadAndSaveFile(presignedUrl: String, saveFilePath: String) {
            val client = OkHttpClient()
            val request = Request.Builder().url(presignedUrl).build()
            var result = false
            while(!result){
                try{
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) throw Exception("Failed to download file: ${response.code} ${response.message}")

                        val body = response.body ?: throw Exception("Empty response body")
                        val file = File(saveFilePath)

                        FileOutputStream(file).use { outputStream ->
                            body.byteStream().use { inputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                        result = true
                    }
                }catch (_: Exception){

                }
            }
    }
}