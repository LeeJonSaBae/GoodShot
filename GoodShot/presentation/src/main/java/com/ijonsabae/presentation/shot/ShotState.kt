package com.ijonsabae.presentation.shot

/**
 * ShotState의 변화에 따라 Z플립 아래 부분에 보일 프래그먼트가 바뀌어야 합니다.
 */
enum class ShotState {
    POSITIONING,  // 전신이 모두 보이도록 조금 더 뒤로 가주세요!
    ADDRESS,      // 어드레스 자세를 잡아주세요!
    SWING,        // 스윙해주세요!
    ANALYZING,    // 스윙 영상 분석중...
    RESULT        // 스윙 분석 결과
}