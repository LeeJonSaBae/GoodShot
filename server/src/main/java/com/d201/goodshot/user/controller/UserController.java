package com.d201.goodshot.user.controller;

import com.d201.goodshot.global.base.BaseResponse;
import com.d201.goodshot.global.security.dto.CustomUser;
import com.d201.goodshot.global.security.dto.Token;
import com.d201.goodshot.global.security.dto.TokenResponse;
import com.d201.goodshot.global.security.exception.InvalidTokenException;
import com.d201.goodshot.user.dto.UserRequest;
import com.d201.goodshot.user.dto.UserRequest.ChangePasswordRequest;
import com.d201.goodshot.user.dto.UserRequest.EmailRequest;
import com.d201.goodshot.user.dto.UserRequest.JoinRequest;
import com.d201.goodshot.user.dto.UserRequest.LoginRequest;
import com.d201.goodshot.user.service.EmailService;
import com.d201.goodshot.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "User")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    @PostMapping("/join")
    @Operation(summary = "회원가입", description = "프로필 이미지는 넣지 않아도 기본 이미지가 들어갑니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "회원가입에 성공했습니다.",
                    content = @Content(mediaType = "",
                    examples = @ExampleObject(value = "")))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<Void> join(@RequestBody JoinRequest joinRequest) {
        userService.join(joinRequest);
        return BaseResponse.of(HttpStatus.CREATED, "회원가입에 성공했습니다.", null);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "로그인에 성공했습니다.",
                    content = @Content(mediaType = "",
                            examples = @ExampleObject(value = "")))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        Token token = userService.login(loginRequest);
        TokenResponse response = TokenResponse.builder().accessToken(token.getAccessToken()).refreshToken(token.getRefreshToken()).build();
        return BaseResponse.created(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그아웃에 성공했습니다.",
                    content = @Content(mediaType = "",
                            examples = @ExampleObject(value = "")))
    })
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<Void> logout(@AuthenticationPrincipal CustomUser customUser) {
        userService.logout(customUser.getEmail());
        return BaseResponse.of(HttpStatus.OK, "로그아웃에 성공했습니다.", null);
    }

    @PostMapping("/exit")
    @Operation(summary = "회원탈퇴", description = "")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원탈퇴에 성공했습니다.",
                    content = @Content(mediaType = "",
                            examples = @ExampleObject(value = "")))
    })
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<Void> exit(@AuthenticationPrincipal CustomUser customUser) {
        userService.exit(customUser.getEmail());
        return BaseResponse.of(HttpStatus.OK, "회원탈퇴에 성공했습니다.", null);
    }

    // Token 재발급
    @PostMapping("/reissue")
    @Operation(summary = "Token 재발급", description = "기존의 refresh token 으로 access, refresh token 둘 다 재발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Token 재발급에 성공했습니다.",
                    content = @Content(mediaType = "",
                            examples = @ExampleObject(value = "")))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<TokenResponse> reissue(@RequestHeader(value = "refreshToken") String refreshToken, HttpServletRequest request) {
        if (StringUtils.isEmpty(refreshToken)) {
            throw new InvalidTokenException();
        }
        Token token = userService.reissue(refreshToken, request);
        TokenResponse response = TokenResponse.builder().accessToken(token.getAccessToken()).refreshToken(token.getRefreshToken()).build();
        return BaseResponse.created(response);
    }

    // 비밀번호 변경
    @PutMapping("/password")
    @Operation(summary = "비밀번호 변경", description = "")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "비밀번호 변경에 성공했습니다.",
                    content = @Content(mediaType = "",
                            examples = @ExampleObject(value = "")))
    })
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<Void> changePassword(@AuthenticationPrincipal CustomUser customUser, @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(customUser.getEmail(), changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());
        return BaseResponse.of(HttpStatus.OK, "비밀번호를 변경했습니다", null);
    }

    // 이메일 전송
    @PostMapping("/email")
    @Operation(summary = "인증번호 이메일 전송", description = "")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "이메일 전송에 성공했습니다.",
                    content = @Content(mediaType = "",
                            examples = @ExampleObject(value = "")))
    })
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<Void> sendEmail(@RequestBody EmailRequest emailRequest) {
        emailService.sendEmail(emailRequest.getEmail());
        return BaseResponse.of(HttpStatus.OK, "이메일 전송에 성공했습니다.", null);
    }

}
