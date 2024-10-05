package com.d201.goodshot.swing.service;

import com.d201.goodshot.global.security.dto.CustomUser;
import com.d201.goodshot.swing.dto.SwingResponse.ReportResponse;
import com.d201.goodshot.swing.dto.SwingResponse.SwingDataResponse;
import com.d201.goodshot.swing.repository.CommentRepository;
import com.d201.goodshot.swing.repository.SwingRepository;
import com.d201.goodshot.user.domain.User;
import com.d201.goodshot.user.exception.NotFoundUserException;
import com.d201.goodshot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SwingService {

    private final SwingRepository swingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    // 종합 리포트 조회
    public ReportResponse getReport(CustomUser customUser) {

        User user = userRepository.findByEmail(customUser.getEmail()).orElseThrow(NotFoundUserException::new);

        // 1. 해당 user 에 대한 Comment 전부 가져와서

        // 2. 데이터 전처리 (반복적으로 문제가 있었던 Comment 3개만 뽑기)

        // 3. Chat GPT API 활용 (자세 교정에 대한 종합 의견 5줄로 달라하기)

        return ReportResponse.builder()
                .name(user.getName())
                .build();
    }

    // 스윙 내보내기
    public void postSwingData(CustomUser customUser) {

    }

    // 스윙 가져오기
    public List<SwingDataResponse> getSwingData(CustomUser customUser) {
        return null;
    }

}
