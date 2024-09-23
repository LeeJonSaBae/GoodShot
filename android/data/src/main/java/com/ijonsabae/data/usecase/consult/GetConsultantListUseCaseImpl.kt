package com.ijonsabae.data.usecase.consult

import com.ijonsabae.domain.model.Consultant
import com.ijonsabae.domain.usecase.consult.GetConsultantListUseCase
import javax.inject.Inject

class GetConsultantListUseCaseImpl @Inject constructor() : GetConsultantListUseCase {
    private val dummyData = Consultant(
        name = "김밍깅",
        profileImage = "https://goodshot-bucket.s3.ap-northeast-2.amazonaws.com/goodshot/default_profile.webp",
        course = "전문가",
        career = 2,
        expertise = "스윙 분석",
        certification = listOf("PGA 인증", "골프 피트니스 전문가 자격증"),
        topic = listOf("스윙 교정", "골프 부상 예방", "체형 교정")
    )
    override fun invoke(): Result<List<Consultant>> {
        return kotlin.runCatching {
            listOf(
                dummyData,
                dummyData,
                dummyData,
                dummyData,
                dummyData,
            )
        }
    }
}