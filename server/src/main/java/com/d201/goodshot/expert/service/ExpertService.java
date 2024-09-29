package com.d201.goodshot.expert.service;

import com.d201.goodshot.expert.repository.ExpertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpertService {

    private final ExpertRepository expertRepository;



}
