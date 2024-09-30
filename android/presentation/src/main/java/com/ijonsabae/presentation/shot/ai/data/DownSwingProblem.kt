package com.ijonsabae.presentation.shot.ai.data

enum class DownSwingProblem(
    private val badCommentTemplate: String,
    private val niceCommentTemplate: String,
    private val isRightHanded: Boolean
) {
    // 우타 기준
    RIGHT_HEAD_MOVEMENT("머리 위치가 %s으로 치우쳐 있습니다.", "머리 위치가 잘 고정되어 있습니다.", true),
    RIGHT_HIP_HEIGHT("골반 높이가 %s으로 치우쳐 있습니다.", "골반 높이가 잘 유지되고 있습니다.", true),
    RIGHT_IMPACT_HAND_HEIGHT("임팩트 시 손 높이가 준비 자세보다 %s에 있습니다.", "임팩트 시 손 높이가 준비 자세와 일치합니다.", true),
    RIGHT_KNEE_SWAY("오른쪽 무릎이 %s으로 과도하게 굽혀져 있습니다.", "무릎 이동이 부드럽게 잘 되고 있습니다.", true),
    RIGHT_HEAD_POSITION_IMPACT("임팩트 시 머리 위치가 공보다 %s에 있습니다.", "임팩트 시 머리 위치가 적절합니다.", true),

    // 좌타 기준
    LEFT_HEAD_MOVEMENT("머리 위치가 %s으로 치우쳐 있습니다.", "머리 위치가 잘 고정되어 있습니다.", false),
    LEFT_HIP_HEIGHT("골반 높이가 %s으로 치우쳐 있습니다.", "골반 높이가 잘 유지되고 있습니다.", false),
    LEFT_IMPACT_HAND_HEIGHT("임팩트 시 손 높이가 준비 자세보다 %s에 있습니다.", "임팩트 시 손 높이가 준비 자세와 일치합니다.", false),
    LEFT_KNEE_SWAY("왼쪽 무릎이 %s으로 과도하게 굽혀져 있습니다.", "무릎 이동이 부드럽게 잘 되고 있습니다.", false),
    LEFT_HEAD_POSITION_IMPACT("임팩트 시 머리 위치가 공보다 너무 %s에 있습니다.", "임팩트 시 머리 위치가 적절합니다.", false);

    fun getBadComment(direction: Direction): String {
        val directionText = when (direction) {
            Direction.TOP -> "위쪽"
            Direction.BOTTOM -> "아래쪽"
            Direction.LEFT -> if (isRightHanded) "왼쪽" else "오른쪽"
            Direction.RIGHT -> if (isRightHanded) "오른쪽" else "왼쪽"
            Direction.FRONT -> "앞쪽"
            Direction.BACK ->  "뒤쪽"
            Direction.CENTER -> "중앙"
        }
        return badCommentTemplate.format(directionText)
    }

    fun getNiceComment(): String {
        return niceCommentTemplate
    }
}