package com.ijonsabae.presentation.shot.ai.data

import com.bumptech.glide.Glide

enum class Solution(
    private val rightHandedSolution: String,
    private val leftHandedSolution: String
) {
    // 백스윙
    BACK_BODY_LIFT(
        "백스윙 시 상체가 너무 크게 들렸어요. 머리를 고정하고 클럽을 뒤로 천천히 드는 연습을 해보세요.",
        "백스윙 시 상체가 너무 크게 들렸어요. 머리를 고정하고 클럽을 뒤로 천천히 드는 연습을 해보세요.",
    ),
    BACK_ARM_EXTENSION(
        "백스윙 과정에서 왼팔이 굽혀져 있어요. 상체를 중심으로 회전하며 오버스윙을 줄이는 연습이 필요합니다.",
        "백스윙 과정에서 오른팔이 굽혀져 있어요. 상체를 중심으로 회전하며 오버스윙을 줄이는 연습이 필요합니다.",
    ),
    BACK_WEIGHT_TRANSFER(
        "백스윙 시 체중이 오른쪽으로 이동하지 않았어요. 백스윙에서 오른쪽 발에 체중을 실어보세요.",
        "백스윙 시 체중이 왼쪽으로 이동하지 않았어요. 백스윙에서 왼쪽 발에 체중을 실어보세요.",
    ),
    BACK_BODY_BALANCE(
        "백스윙 동작에서 골반이 기울어졌어요. 상체를 고정한 뒤 골반을 회전하는 연습을 해보세요.",
        "백스윙 동작에서 골반이 기울어졌어요. 상체를 고정한 뒤 골반을 회전하는 연습을 해보세요.",
    ),

    // 다운스윙
    DOWN_BODY_LIFT(
        "백스윙과 다운스윙 궤도가 일정하게 유지되지 못했어요. 상체와 팔의 각도를 유지한 상태로 스윙을 해보세요.",
        "백스윙과 다운스윙 궤도가 일정하게 유지되지 못했어요. 상체와 팔의 각도를 유지한 상태로 스윙을 해보세요.",
    ),
    DOWN_WEIGHT_TRANSFER(
        "다운스윙에서 체중이 왼쪽으로 충분히 이동하지 않았어요. 임팩트 시 왼쪽 발에 체중을 실어보세요.",
        "다운스윙에서 체중이 오른쪽으로 충분히 이동하지 않았어요. 임팩트 시 오른쪽 발에 체중을 실어보세요.",
    ),
    DOWN_BODY_BALANCE(
        "다운스윙 시 상체에 비해 하체 회전이 너무 빨라요. 왼발을 중심축으로 상체를 회전하는 연습을 해보세요.",
        "다운스윙 시 상체에 비해 하체 회전이 너무 빨라요. 왼발을 중심축으로 상체를 회전하는 연습을 해보세요.",
    ),

    // 문제점 없을 때
    GOOD_SHOT(
        "굿샷~!, 좋은 스윙이에요! 현재의 폼을 유지하면서 일관성을 높이는 연습을 해보세요.",
        "굿샷~!, 좋은 스윙이에요! 현재의 폼을 유지하면서 일관성을 높이는 연습을 해보세요.",
    );

    fun getSolution(isRightHanded: Boolean): String {
        return if (isRightHanded) rightHandedSolution else leftHandedSolution
    }
}
