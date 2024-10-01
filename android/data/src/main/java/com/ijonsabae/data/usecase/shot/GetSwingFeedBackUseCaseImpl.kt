package com.ijonsabae.data.usecase.shot

import com.ijonsabae.domain.model.FeedBack
import com.ijonsabae.domain.usecase.shot.GetSwingFeedBackUseCase
import javax.inject.Inject

class GetSwingFeedBackUseCaseImpl @Inject constructor() : GetSwingFeedBackUseCase {
    private val feedBack = FeedBack(
        down = 0.40F,
        tempo = 1.67F,
        back = 0.67F,
        feedBackSolution = "백스윙 시 상체가 너무 크게 들렸어요. 백스윙 크기를 줄이는 연습이 필요합니다!",
        feedBackCheckListTitle = "올바른 백스윙 자세를 확인하실 떄는 간단히 다음 항목들을 확인해보세요.",
        feedBackCheckList = listOf(
            "어드레스부터 백스윙 탑까지 상하체 간격 유지하기",
            "어깨 회전과 팔을 함께 사용해 백스윙 탑 만들기",
            "척추 각도는 어드레스와 같은 상태로 유지하기",
            "백스윙 탑에서 손목과 클럽 페이스가 같은 방향을 바라보도록 하고, 손등 펴기"
        )
    )
    override fun invoke(): Result<FeedBack> {
        return kotlin.runCatching {
            feedBack
        }
    }
}