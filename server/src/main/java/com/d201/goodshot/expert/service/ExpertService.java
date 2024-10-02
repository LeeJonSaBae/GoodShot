package com.d201.goodshot.expert.service;

import com.d201.goodshot.expert.domain.Expert;
import com.d201.goodshot.expert.dto.ExpertResponse.ExpertDetailItem;
import com.d201.goodshot.expert.dto.ExpertResponse.ExpertItem;
import com.d201.goodshot.expert.dto.ExpertResponse.ExpertItemResponse;
import com.d201.goodshot.expert.repository.ExpertRepository;
import com.d201.goodshot.user.exception.NotFoundUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpertService {

    private final ExpertRepository expertRepository;

    public ExpertItemResponse getExpertList(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Slice<Expert> expertSlice = expertRepository.findAllBy(pageRequest);
        List<Expert> experts = expertSlice.getContent();

        List<ExpertItem> expertItems = experts
                .stream().map(expert -> ExpertItem.
                            builder()
                            .id(expert.getId())
                            .imageUrl(expert.getProfileUrl())
                            .name(expert.getName())
                            .field(expert.getField())
                            .expYears(expert.getExpYears())
                            .counselUrl(expert.getCounselUrl())
                            .build())
                .collect(Collectors.toList());

        return ExpertItemResponse.
                builder()
                .experts(expertItems)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .hasNext(expertSlice.hasNext())
                .build();
    }

    public ExpertDetailItem getExpertDetail(int id) {
        Expert expert = expertRepository.findById(id).orElseThrow(NotFoundUserException::new);

        List<String> certificates = Arrays.asList(expert.getCertificate().split(", "));

        return ExpertDetailItem
                .builder()
                .imageUrl(expert.getProfileUrl())
                .name(expert.getName())
                .certificates(certificates)
                .expYears(expert.getExpYears())
                .build();
    }

}
