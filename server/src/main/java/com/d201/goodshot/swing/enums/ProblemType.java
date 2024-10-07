package com.d201.goodshot.swing.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProblemType {

    // 우타 기준
    RIGHT_HEAD_MOVEMENT("머리 위치가 %s으로 치우쳐 있습니다."),
    RIGHT_HIP_ROTATION("골반이 과도하게 바깥쪽으로 회전합니다."),
    RIGHT_ARM_BEND("왼팔이 안쪽으로 구부러져 있습니다."),
    RIGHT_KNEE_SWAY("오른쪽 무릎으로의 체중 이동이 잘되지 않고 있습니다."),
    RIGHT_FRONT_FOOT_LIFT("탑 스윙에서 앞발이 들려 있습니다."),

    // 좌타 기준
    LEFT_HEAD_MOVEMENT("머리 위치가 %s로 치우쳐 있습니다."),
    LEFT_HIP_ROTATION("골반이 과도하게 바깥쪽으로 회전합니다."),
    LEFT_ARM_BEND("오른팔이 안쪽으로 구부러져 있습니다."),
    LEFT_KNEE_SWAY("왼쪽 무릎으로의 체중 이동이 잘되지 않고 있습니다."),
    LEFT_FRONT_FOOT_LIFT("탑 스윙에서 앞발이 들려 있습니다.");

    private final String comment;

}
