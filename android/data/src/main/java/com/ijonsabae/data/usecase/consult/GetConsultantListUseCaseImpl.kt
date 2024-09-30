package com.ijonsabae.data.usecase.consult

import com.ijonsabae.domain.model.Consultant
import com.ijonsabae.domain.usecase.consult.GetConsultantListUseCase
import javax.inject.Inject

class GetConsultantListUseCaseImpl @Inject constructor() : GetConsultantListUseCase {
    private val dummyData = Consultant(
        name = "김밍깅",
        profileImage = "https://goodshot-bucket.s3.ap-northeast-2.amazonaws.com/goodshot/default_profile.webp",
        pro = "KPGA PRO",
        career = 2,
        phoneNumber = "010-1234-5678",
        certification = listOf("PGA 인증", "골프 피트니스 전문가 자격증"),
        chatUrl = "https://open.kakao.com/o/sc4oiORg"
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