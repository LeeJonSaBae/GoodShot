package com.ijonsabae.presentation.shot.ai.data

enum class BackSwingProblem(
    private val badCommentTemplate: String,
    private val niceCommentTemplate: String,
    private val isRightHanded: Boolean
) {
    // 우타 기준
    RIGHT_HEAD_MOVEMENT("머리 위치가 %s으로 치우쳐 있습니다.", "머리 위치가 잘 고정되어 있습니다.", true),
    RIGHT_HIP_ROTATION("골반이 과도하게 바깥쪽으로 회전합니다.", "골반 회전이 적절합니다.", true),
    RIGHT_ARM_BEND("왼팔이 안쪽으로 구부러져 있습니다.", "왼팔이 잘 펴져 있습니다.", true),
    RIGHT_KNEE_SWAY("왼쪽 무릎이 과도하게 굽혀져 있습니다.", "무릎 이동이 부드럽게 잘 되고 있습니다.", true),
    RIGHT_FRONT_FOOT_LIFT("탑 스윙에서 앞발이 들려 있습니다.", "앞발이 지면에 잘 고정되어 있습니다.", true),

    // 좌타 기준
    LEFT_HEAD_MOVEMENT("머리 위치가 %s로 치우쳐 있습니다.", "머리 위치가 잘 고정되어 있습니다.", false),
    LEFT_HIP_ROTATION("골반이 과도하게 바깥쪽으로 회전합니다.", "골반 회전이 적절합니다.", false),
    LEFT_ARM_BEND("오른팔이 안쪽으로 구부러져 있습니다.", "오른팔이 잘 펴져 있습니다.", false),
    LEFT_KNEE_SWAY("오른쪽 무릎이 과도하게 굽혀져 있습니다.", "무릎 이동이 부드럽게 잘 되고 있습니다.", false),
    LEFT_FRONT_FOOT_LIFT("탑 스윙에서 앞발이 들려 있습니다.", "앞발이 지면에 잘 고정되어 있습니다.", false);

    fun getBadComment(direction: Direction): String {
        val directionText = when (direction) {
            Direction.TOP -> "위쪽"
            Direction.BOTTOM -> "아래쪽"
            Direction.LEFT -> if (isRightHanded) "왼쪽" else "오른쪽"
            Direction.RIGHT -> if (isRightHanded) "오른쪽" else "왼쪽"
            Direction.CENTER -> "중앙"
        }
        return badCommentTemplate.format(directionText)
    }

    fun getNiceComment(): String {
        return niceCommentTemplate
    }
}