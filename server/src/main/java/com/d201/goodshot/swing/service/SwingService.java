package com.d201.goodshot.swing.service;

import com.d201.goodshot.global.security.dto.CustomUser;
import com.d201.goodshot.swing.domain.Comment;
import com.d201.goodshot.swing.domain.Swing;
import com.d201.goodshot.swing.domain.SwingImage;
import com.d201.goodshot.swing.dto.CommentItem;
import com.d201.goodshot.swing.dto.SwingRequest;
import com.d201.goodshot.swing.dto.SwingRequest.SwingDataRequest;
import com.d201.goodshot.swing.dto.SwingResponse;
import com.d201.goodshot.swing.dto.SwingResponse.SwingDataResponse;
import com.d201.goodshot.swing.enums.PoseType;
import com.d201.goodshot.swing.exception.SwingImageProcessingException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class SwingService {

    private final SwingRepository swingRepository;
    private final CommentRepository commentRepository;
    private final SwingImageRepository swingImageRepository;
    private final UserRepository userRepository;

    // 스윙 데이터 가져오기
    public List<SwingDataResponse> getSwingData(CustomUser customUser) {

        User user = userRepository.findByEmail(customUser.getEmail()).orElseThrow(NotFoundUserException::new);
        List<Swing> swings = swingRepository.findByUser(user); // 사용자 스윙 List 찾기
        List<SwingDataResponse> swingDataList = new ArrayList<>();

        // swing list 돌면서
        for(Swing swing : swings) {
            // DB 에 있는 데이터 전부 전송
            SwingDataResponse swingData = SwingDataResponse.builder()
                    .id(swing.getId())
                    .backSwingComments(convertCommentsToCommentItems(swing.getComments(), PoseType.BACK)) // 백스윙 코멘트 변환
                    .downSwingComments(convertCommentsToCommentItems(swing.getComments(), PoseType.DOWN)) // 다운스윙 코멘트 변환
                    .swingVideo(swing.getSwingVideo()) // 스윙 비디오 데이터
                    .likeStatus(swing.isLikeStatus()) // 좋아요 상태
                    .poseSimilarity(swing.getSimilarity()) // 포즈 유사도 변환
                    .swingImages(convertSwingImagesToBytes(swing.getSwingImages())) // 스윙 이미지
                    .score(swing.getScore()) // 스코어
                    .solution(swing.getSolution()) // 솔루션
                    .title(swing.getTitle()) // 제목
                    .time(swing.getTime()) // 시간
                    .tempo(swing.getTempo()) // 템포
                    .build();

            swingDataList.add(swingData);
        }

        return swingDataList;
    }

    // 스윙 데이터 내보내기
    public void postSwingData(List<SwingDataRequest> swingDataList, CustomUser customUser) throws IOException {

        User user = userRepository.findByEmail(customUser.getEmail()).orElseThrow(NotFoundUserException::new);
        List<Swing> swings = swingRepository.findByUser(user);

        // SwingData list 돌면서 swing, swingImage, comment 찾기
        for(SwingDataRequest swingData : swingDataList) {
            boolean isNewSwing = true;

            // 기존 스윙 리스트에서 동일한 id 찾기
            for (Swing swing : swings) {
                if (Objects.equals(swing.getId(), swingData.getId())) {
                    // 시간을 비교하여 받아온 데이터가 더 최신일 경우에만 갱신
                    if (swingData.getTime().isAfter(swing.getTime())) {
                        // swing 이미지 업데이트
                        swingImageRepository.deleteAll(swing.getSwingImages());
                        List<SwingImage> newSwingImages = convertMultipartFilesToSwingImages(swingData.getSwingImages(), swing);
                        swingImageRepository.saveAll(newSwingImages);

                        // 코멘트 업데이트 (기존 코멘트를 삭제 후 새로 저장)
                        commentRepository.deleteAll(swing.getComments());
                        List<Comment> newComments = convertCommentItemsToComments(swingData.getBackSwingComments(), swingData.getDownSwingComments(), swing);
                        commentRepository.saveAll(newComments);

                        // 변경된 Swing 데이터 업데이트
                        swing.updateSwing(swingData);
                    }
                    isNewSwing = false; // 기존 Swing 데이터가 있으므로 새로운 Swing 이 아님
                    break;
                }
            }

            if (isNewSwing) {

                Swing newSwing = Swing.builder()
                        .user(user)
                        .solution(swingData.getSolution())
                        .score(swingData.getScore())
                        .tempo(swingData.getTempo())
                        .likeStatus(swingData.isLikeStatus())
                        .title(swingData.getTitle())
                        .time(swingData.getTime())
                        .swingVideo(swingData.getSwingVideo().getBytes())
                        .similarity(swingData.getPoseSimilarity().toString())
                        .build();

                List<SwingImage> newSwingImages = convertMultipartFilesToSwingImages(swingData.getSwingImages(), newSwing);
                swingImageRepository.saveAll(newSwingImages);

                List<Comment> newComments = convertCommentItemsToComments(swingData.getBackSwingComments(), swingData.getDownSwingComments(), newSwing);
                commentRepository.saveAll(newComments);

                swingRepository.save(newSwing);
            }
        }
    }

    // 이미지 Byte Type 으로 변환
    private List<byte[]> convertSwingImagesToBytes(List<SwingImage> swingImages) {
        List<byte[]> imageBytesList = new ArrayList<>();

        for (SwingImage swingImage : swingImages) {
            imageBytesList.add(swingImage.getSwingImage()); // byte[] 데이터를 리스트에 추가
        }

        return imageBytesList;
    }

    // Swing 이미지 변환
    private List<SwingImage> convertMultipartFilesToSwingImages(List<MultipartFile> files, Swing swing) {
        List<SwingImage> swingImages = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            try {
                SwingImage swingImage = SwingImage.builder()
                        .swing(swing)
                        .poseIndex(i)
                        .swingImage(file.getBytes())
                        .build();
                swingImages.add(swingImage);
            } catch (IOException e) {
                throw new SwingImageProcessingException();
            }
        }

        return swingImages;
    }

    // CommentItem 을 Comment 로 변환
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

}
