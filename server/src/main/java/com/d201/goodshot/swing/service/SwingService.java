package com.d201.goodshot.swing.service;

import com.d201.goodshot.global.s3.dto.ImageRequest;
import com.d201.goodshot.global.s3.dto.ImageRequest.PresignedUrlRequest;
import com.d201.goodshot.global.s3.service.S3Service;
import com.d201.goodshot.global.security.dto.CustomUser;
import com.d201.goodshot.swing.domain.Comment;
import com.d201.goodshot.swing.domain.Swing;
import com.d201.goodshot.swing.dto.CommentItem;
import com.d201.goodshot.swing.dto.SwingData;
import com.d201.goodshot.swing.dto.SwingRequest.SwingDataRequest;
import com.d201.goodshot.swing.dto.SwingResponse.ReportResponse;
import com.d201.goodshot.swing.dto.SwingResponse.SwingCodeResponse;
import com.d201.goodshot.swing.enums.PoseType;
import com.d201.goodshot.swing.exception.SwingJsonProcessingException;
import com.d201.goodshot.swing.repository.CommentRepository;
import com.d201.goodshot.swing.repository.SwingRepository;
import com.d201.goodshot.user.domain.User;
import com.d201.goodshot.user.exception.NotFoundUserException;
import com.d201.goodshot.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.http.HttpHeaders;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SwingService {

    private final SwingRepository swingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    // 종합 리포트 조회
    public ReportResponse getReport(CustomUser customUser) {

        User user = userRepository.findByEmail(customUser.getEmail()).orElseThrow(NotFoundUserException::new);
        List<Swing> swings = swingRepository.findByUser(user); // swing 전부 찾기

        double totalScore = 0.0;

        // 유사도 값을 누적할 맵 (8가지 자세에 대한 유사도를 저장)
        Map<String, Double> cumulativeSimilarity = new HashMap<>();
        // 유사도를 0으로 초기화
        cumulativeSimilarity.put("address", 0.0);
        cumulativeSimilarity.put("toeUp", 0.0);
        cumulativeSimilarity.put("midBackSwing", 0.0);
        cumulativeSimilarity.put("top", 0.0);
        cumulativeSimilarity.put("midDownSwing", 0.0);
        cumulativeSimilarity.put("impact", 0.0);
        cumulativeSimilarity.put("midFollowThrough", 0.0);
        cumulativeSimilarity.put("finish", 0.0);

        ObjectMapper objectMapper = new ObjectMapper(); // JSON 파서를 위한 ObjectMapper 생성

        // Comment content 빈도를 저장할 맵
        Map<String, Integer> commentFrequency = new HashMap<>();
        int swingCount = swings.size();

        // 1. 해당 user 에 대한 Comment 전부 가져와서
        for(Swing swing : swings) {
            List<Comment> comments = swing.getComments(); // 하나의 스윙에 Comment 는 10개

            for (Comment comment : comments) {
                // Comment의 content 가져오기
                String content = comment.getContent();

                // content의 빈도를 카운트
                commentFrequency.put(content, commentFrequency.getOrDefault(content, 0) + 1);
            }

            // 점수
            totalScore += swing.getScore();

            // 유사도 (0 ~ 1 사이값) : 100 곱해줘서 계산해야 함
            String similarity = swing.getSimilarity(); // json 형식
            // 8가지 자세 각 value 값 뽑아서 더하기
            // 유사도 처리 (JSON 형식 파싱)
            String similarityJson = swing.getSimilarity(); // JSON 형식 문자열
            try {
                // JSON 문자열을 Map<String, Double> 형식으로 파싱
                Map<String, Double> similarityMap = objectMapper.readValue(similarityJson, new TypeReference<Map<String, Double>>() {});

                // 8가지 자세의 유사도를 누적하여 더함
                for (Map.Entry<String, Double> entry : similarityMap.entrySet()) {
                    String poseType = entry.getKey();
                    Double value = entry.getValue();
                    cumulativeSimilarity.put(poseType, cumulativeSimilarity.get(poseType) + (value * 100)); // 100을 곱한 값을 더함
                }

            } catch (IOException e) {
                throw new SwingJsonProcessingException();
            }

        }

        double averageScore = totalScore / swingCount;

        // 2. 데이터 전처리 (반복적으로 문제가 있었던 Comment 3개만 뽑기)
        List<String> top3Comments = commentFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) // 빈도 순으로 내림차순 정렬
                .limit(3) // 상위 3개 선택
                .map(Map.Entry::getKey) // 키 (comment content)만 추출
                .toList();

        // 3. Chat GPT API 활용 (3가지 Comment 백스윙, 다운스윙 나누어서 주고 자세 교정에 대한 종합 의견 5줄로 달라하기)
        String totalComment = callGptApi(top3Comments);

        // 8가지 자세에 대한 평균 유사도 계산
        List<Double> averageSimilarity = cumulativeSimilarity.values().stream()
                .map(aDouble -> aDouble / swingCount) // 누적된 값을 스윙 개수로 나눔
                .toList();

        return ReportResponse.builder()
                .name(user.getName())
                .score(averageScore)
                .problems(top3Comments)
                .similarity(averageSimilarity)
                .content(totalComment)
                .build();
    }

    private String callGptApi(List<String> top3Comments) {
        String apiKey = "YOUR_OPENAI_API_KEY";  // OpenAI API 키
        String url = "https://api.openai.com/v1/completions"; // url

        // ChatGPT  프롬프트 생성
        String prompt = top3Comments.get(0) + ", " + top3Comments.get(1) + ", " + top3Comments.get(2) + ". 3가지가 골프 스윙할 때 가장 문제가 큰 부분이야. 이 부분을 고치려면 어떻게 해야하는지 5줄로 자세하게 피드백을 해줘."

        return null;
    }

    // 스윙 가져오기
    public List<SwingData> importSwingData(CustomUser customUser, SwingDataRequest swingDataRequest) {
        User user = userRepository.findByEmail(customUser.getEmail()).orElseThrow(NotFoundUserException::new);
        List<Swing> swings = swingRepository.findByUser(user); // 해당 사용자에 대한 swing 전부 찾아오기
        Set<String> codesSet = new HashSet<>(swingDataRequest.getCodes());

        // 받아온 swingDataRequest 내부에 있는 codes 를 돌면서
        // swings 에 있는 code 랑 비교해서
        // 서버에만 있는 스윙 데이터 return

        // 서버에만 있는 스윙 데이터를 필터링
        return swings.stream()
                .filter(swing -> !codesSet.contains(swing.getCode())) // 서버에만 있는 데이터를 필터링
                .map(swing -> SwingData.builder()
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

    // 스윙 비교하기 (Room 에 데이터가 더 많은 상태)
    public List<SwingCodeResponse> compareSwingData(CustomUser customUser, SwingDataRequest swingDataRequest) {
        // 사용자 조회
        User user = userRepository.findByEmail(customUser.getEmail()).orElseThrow(NotFoundUserException::new);

        // 사용자의 기존 Swing 리스트 조회
        List<Swing> swings = swingRepository.findByUser(user);
        List<String> codes = swingDataRequest.getCodes();

        // 결과 리스트 생성
        List<SwingCodeResponse> swingCodeResponses = new ArrayList<>();

        // 각 요청된 코드에 대해 처리
        for (String code : codes) {
            // Swing 리스트에서 해당 코드가 있는지 확인
            boolean exists = swings.stream().anyMatch(swing -> swing.getCode().equals(code));

            // 해당 코드가 없으면 Presigned URL 생성 및 응답에 추가
            if (!exists) {
                List<String> presignedUrls = new ArrayList<>();

                // 1. video presigned URL 발급
                PresignedUrlRequest presignedUrlVideoRequest = new PresignedUrlRequest(ImageRequest.ImageExtension.MP4);
                presignedUrls.add(s3Service.issuePresignedUrl(presignedUrlVideoRequest, user.getId(), "video", code).getPresignedUrl());

                // 2. thumbnail presigned URL 발급
                PresignedUrlRequest presignedUrlThumbnailRequest = new PresignedUrlRequest(ImageRequest.ImageExtension.JPG);
                presignedUrls.add(s3Service.issuePresignedUrl(presignedUrlThumbnailRequest, user.getId(), "thumbnail", code).getPresignedUrl());

                // 3. image presigned URLs 발급 (8개 이미지)
                for (int i = 0; i < 8; i++) {
                    PresignedUrlRequest presignedUrlImageRequest = new PresignedUrlRequest(ImageRequest.ImageExtension.JPG);
                    presignedUrls.add(s3Service.issuePresignedUrl(presignedUrlImageRequest, user.getId(), "image", code + "_" + i).getPresignedUrl());
                }

                // 응답 객체 생성 후 리스트에 추가
                swingCodeResponses.add(SwingCodeResponse.builder()
                        .code(code)
                        .presignedUrls(presignedUrls)
                        .build());
            }
        }

        return swingCodeResponses;
    }

    // 스윙 내보내기
    public void exportSwingData(CustomUser customUser, List<SwingData> swingDataList) {

        User user = userRepository.findByEmail(customUser.getEmail()).orElseThrow(NotFoundUserException::new);

        // 받아온 데이터 DB 에 저장
        for(SwingData swingData : swingDataList) {

            // swing 저장
            Swing swing = Swing.builder()
                    .code(swingData.getCode())
                    .user(user)
                    .likeStatus(swingData.getLikeStatus())
                    .similarity(swingData.getSimilarity())
                    .solution(swingData.getSolution())
                    .score(swingData.getScore())
                    .tempo(swingData.getTempo())
                    .title(swingData.getTitle())
                    .time(swingData.getTime())
                    .build();

            swingRepository.save(swing);

            // comment 저장 (convertCommentItemsToComments 메서드 활용)
            List<Comment> comments = convertCommentItemsToComments(swingData.getBackSwingComments(), swingData.getDownSwingComments(), swing);
            commentRepository.saveAll(comments); // 모든 코멘트를 한 번에 저장

        }

    }

    private List<Comment> convertCommentItemsToComments(List<CommentItem> backSwingComments, List<CommentItem> downSwingComments, Swing swing) {
        List<Comment> comments = new ArrayList<>();

        // 백 스윙 코멘트 변환
        for (CommentItem commentItem : backSwingComments) {
            Comment comment = Comment.builder()
                    .swing(swing)
                    .poseType(PoseType.BACK)
                    .commentType(commentItem.getCommentType())
                    .content(commentItem.getContent())
                    .build();
            comments.add(comment);
        }

        // 다운 스윙 코멘트 변환
        for (CommentItem commentItem : downSwingComments) {
            Comment comment = Comment.builder()
                    .swing(swing)
                    .poseType(PoseType.DOWN)
                    .commentType(commentItem.getCommentType())
                    .content(commentItem.getContent())
                    .build();
            comments.add(comment);
        }

        return comments;
    }



}
