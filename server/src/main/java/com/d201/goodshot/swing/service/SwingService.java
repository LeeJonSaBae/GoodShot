package com.d201.goodshot.swing.service;

import com.d201.goodshot.global.security.dto.CustomUser;
import com.d201.goodshot.swing.domain.Comment;
import com.d201.goodshot.swing.domain.Swing;
import com.d201.goodshot.swing.domain.SwingImage;
import com.d201.goodshot.swing.dto.SwingData;
import com.d201.goodshot.swing.repository.CommentRepository;
import com.d201.goodshot.swing.repository.SwingImageRepository;
import com.d201.goodshot.swing.repository.SwingRepository;
import com.d201.goodshot.user.domain.User;
import com.d201.goodshot.user.exception.NotFoundUserException;
import com.d201.goodshot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SwingService {

    private final SwingRepository swingRepository;
    private final CommentRepository commentRepository;
    private final SwingImageRepository swingImageRepository;
    private final UserRepository userRepository;

    // 스윙 영상 내보내기
//    public void downloadSwingData(List<SwingData> swingDataList, CustomUser customUser) {
//        for (SwingData swingData : swingDataList) {
//            // 기존 데이터 확인
//            User user = userRepository.findByEmail(customUser.getEmail()).orElseThrow(NotFoundUserException::new);
//            // 존재하는지 확인
//            Swing existingSwing = swingRepository.findByUser(user).orElseThrow(NotFoundUserException::new);
//
//            if (existingSwing == null) {
//                // 기존 데이터가 없으면 새로 저장
//                Swing newSwing = Swing.builder()
//                        .user(user) // 사용자 정보
//                        .swingVideo(swingData.getSwingVideo().getBytes()) // 비디오 데이터
//                        .swingImages(convertMultipartFileListToByteArray(swingData.getSwingImages())) // 이미지 변환
//                        .backSwingComments(swingData.getBackSwingComments()) // 백스윙 코멘트
//                        .downSwingComments(swingData.getDownSwingComments()) // 다운스윙 코멘트
//                        .poseSimilarity(swingData.getPoseSimilarity()) // 유사도
//                        .solution(swingData.getSolution()) // 솔루션
//                        .score(swingData.getScore()) // 점수
//                        .tempo(swingData.getTempo()) // 템포
//                        .likeStatus(swingData.isLikeStatus()) // 좋아요 상태
//                        .title(swingData.getTitle()) // 타이틀
//                        .time(LocalDateTime.now()) // 현재 시간 저장
//                        .build();
//
//                Comment comment = Comment.builder()
//                        .build();
//
//                // 새 데이터 저장
//                swingRepository.save(newSwing);
//                List<SwingImage> swingImages = convertMultipartFilesToSwingImages(swingData.getSwingImages(), newSwing);
//                swingImageRepository.saveAll(swingImages);
//            } else {
//                // 기존 데이터가 있고 시간이 변경된 경우에만 업데이트
//                if (swingData.getTime().isAfter(existingSwing.getTime())) {
//                    // 변경된 데이터 업데이트
//                    // 스윙 변경
//                    // 스윙 이미지 변경
//                    // Comment 변경
//                }
//            }
//        }
//    }
//
//    private List<byte[]> convertMultipartFileListToByteArray(List<MultipartFile> files) {
//        List<byte[]> byteArrayList = new ArrayList<>();
//        for (MultipartFile file : files) {
//            try {
//                byteArrayList.add(file.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return byteArrayList;
//    }
//
//    private List<SwingImage> convertMultipartFilesToSwingImages(List<MultipartFile> files, Swing swing) {
//        List<SwingImage> swingImages = new ArrayList<>();
//        for (int i = 0; i < files.size(); i++) {
//            MultipartFile file = files.get(i);
//            try {
//                SwingImage swingImage = SwingImage.builder()
//                        .swing(swing) // 연관관계 설정
//                        .poseIndex(i) // 이미지 인덱스 저장
//                        .swingImage(file.getBytes()) // 이미지 데이터 저장
//                        .build();
//                swingImages.add(swingImage);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return swingImages;
//    }

}
