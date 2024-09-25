package com.d201.goodshot.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "Email", timeToLive = 300)
public class Email {
    @Id
    private String email;
    private String certificationNumber;
}
