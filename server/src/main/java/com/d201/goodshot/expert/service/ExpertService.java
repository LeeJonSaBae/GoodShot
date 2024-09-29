package com.d201.goodshot.expert.service;

import com.d201.goodshot.expert.domain.Expert;
import com.d201.goodshot.expert.dto.ExpertResponse;
import com.d201.goodshot.expert.dto.ExpertResponse.ExpertItem;
import com.d201.goodshot.expert.dto.ExpertResponse.ExpertItemResponse;
import com.d201.goodshot.expert.repository.ExpertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                            .imageUrl(expert.getProfileUrl())
                            .name(expert.getName())
                            .field(expert.getField())
                            .expYears(expert.getExpYears())
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

}
