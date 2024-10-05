package com.d201.goodshot.swing.service;

import com.d201.goodshot.global.security.dto.CustomUser;
import com.d201.goodshot.swing.domain.Comment;
import com.d201.goodshot.swing.domain.Swing;
import com.d201.goodshot.swing.dto.CommentItem;
import com.d201.goodshot.swing.dto.SwingRequest;
import com.d201.goodshot.swing.dto.SwingRequest.SwingDataRequest;
import com.d201.goodshot.swing.dto.SwingResponse.ReportResponse;
import com.d201.goodshot.swing.dto.SwingResponse.SwingDataResponse;
import com.d201.goodshot.swing.enums.PoseType;
import com.d201.goodshot.swing.repository.CommentRepository;
import com.d201.goodshot.swing.repository.SwingRepository;
import com.d201.goodshot.user.domain.User;
import com.d201.goodshot.user.exception.NotFoundUserException;
import com.d201.goodshot.user.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    // 스윙 가져오기
    public List<SwingDataResponse> importSwingData(CustomUser customUser, SwingDataRequest swingDataRequest) {
        User user = userRepository.findByEmail(customUser.getEmail()).orElseThrow(NotFoundUserException::new);
        List<Swing> swings = swingRepository.findByUser(user); // 해당 사용자에 대한 swing 전부 찾아오기
        Set<String> codesSet = new HashSet<>(swingDataRequest.getCodes());

        // 받아온 swingDataRequest 내부에 있는 codes 를 돌면서
        // swings 에 있는 code 랑 비교해서
        // 서버에만 있는 스윙 데이터 return

        // 서버에만 있는 스윙 데이터를 필터링
        return swings.stream()
                .filter(swing -> !codesSet.contains(swing.getCode())) // 서버에만 있는 데이터를 필터링
                .map(swing -> SwingDataResponse.builder()
                        .id(swing.getId())
                        .similarity(swing.getSimilarity())
                        .solution(swing.getSolution())
                        .score(swing.getScore())
                        .tempo(swing.getTempo())
                        .likeStatus(swing.getLikeStatus())
                        .title(swing.getTitle())
                        .code(swing.getCode())
                        .time(swing.getTime())
                        .backSwingComments(convertCommentsToCommentItems(swing.getComments(), PoseType.BACK))
                        .downSwingComments(convertCommentsToCommentItems(swing.getComments(), PoseType.DOWN))
                        .build())
                .collect(Collectors.toList()); // List로 변환하여 반환

    }

    // comment 를 commentItem 으로 back, down 으로 나누기
    private List<CommentItem> convertCommentsToCommentItems(List<Comment> comments, PoseType poseType) {
        List<CommentItem> commentItems = new ArrayList<>();

        for (Comment comment : comments) {
            // PoseType
            if (comment.getPoseType() == poseType) {
                CommentItem commentItem = CommentItem.builder()
                        .commentType(comment.getCommentType()) // 코멘트 타입 복사
                        .content(comment.getContent()) // 코멘트 내용 복사
                        .build();
                commentItems.add(commentItem);
            }
        }

        return commentItems;
    }


    // 스윙 비교하기


    // 스윙 내보내기
    public void exportSwingData(CustomUser customUser) {

    }


}
