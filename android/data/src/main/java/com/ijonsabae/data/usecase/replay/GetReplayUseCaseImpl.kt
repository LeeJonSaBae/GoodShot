package com.ijonsabae.data.usecase.replay


import com.ijonsabae.domain.model.Replay
import com.ijonsabae.domain.usecase.replay.GetReplayUseCase
import javax.inject.Inject

class GetReplayUseCaseImpl @Inject constructor() : GetReplayUseCase {
    private val replay = Replay(
        "https://goodshot-bucket.s3.ap-northeast-2.amazonaws.com/goodshot/default_profile.webp",
        "제목1",
        "2024년 9월 11일",
        "좌타",
        "아이언",
        false
    )

    override fun invoke(): Result<List<Replay>> {
        return kotlin.runCatching {
            listOf(
                replay,
                replay,
                replay,
                replay
            )
        }
    }

}