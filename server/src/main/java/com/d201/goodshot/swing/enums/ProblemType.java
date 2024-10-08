package com.d201.goodshot.swing.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.d201.goodshot.swing.exception.NotFoundProblemTypeException;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ProblemType {

    // 백스윙
    BACK_HEAD_MOVEMENT("머리 위치가 %s으로 치우쳐 있습니다."),
    BACK_HIP_ROTATION("골반이 과도하게 바깥쪽으로 회전합니다."),
    BACK_RIGHT_ARM_BEND("왼팔이 안쪽으로 구부러져 있습니다."),
    BACK_RIGHT_KNEE_SWAY("오른쪽 무릎으로의 체중 이동이 잘되지 않고 있습니다."),
    BACK_FRONT_FOOT_LIFT("탑 스윙에서 앞발이 들려 있습니다."),
    BACK_LEFT_ARM_BEND("오른팔이 안쪽으로 구부러져 있습니다."),
    BACK_LEFT_KNEE_SWAY("왼쪽 무릎으로의 체중 이동이 잘되지 않고 있습니다."),

    // 다운스윙
    DOWN_HIP_HEIGHT("골반 높이가 %s으로 치우쳐 있습니다."),
    DOWN_IMPACT_HAND_HEIGHT("임팩트 시 손 높이가 준비 자세보다 %s에 있습니다."),
    DOWN_RIGHT_KNEE_SWAY("왼쪽 무릎으로의 체중 이동이 잘되지 않고 있습니다."),
    DOWN_HEAD_POSITION_IMPACT("임팩트 시 머리 위치가 공보다 %s에 있습니다."),
    DOWN_LEFT_KNEE_SWAY("오른쪽 무릎으로의 체중 이동이 잘되지 않고 있습니다.");

    private final String comment;

    // Comment 문자열에 맞는 ProblemType 찾기
    public static ProblemType findByComment(String comment) {
        return Arrays.stream(values())
                .filter(problemType -> comment.contains(problemType.comment.split("%s")[0])) // comment의 고정 부분을 기준으로 매칭
                .findFirst()
                .orElseThrow(NotFoundProblemTypeException::new);
    }

}
