package com.d201.goodshot.user.controller;

import com.d201.goodshot.global.base.BaseResponse;
import com.d201.goodshot.global.security.dto.Token;
import com.d201.goodshot.global.security.dto.TokenResponse;
import com.d201.goodshot.user.dto.UserRequest;
import com.d201.goodshot.user.dto.UserRequest.JoinRequest;
import com.d201.goodshot.user.dto.UserRequest.LoginRequest;
import com.d201.goodshot.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "User")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
        TokenResponse response = TokenResponse.builder().accessToken(token.getAccessToken()).build();
        System.out.println("token : "  + response);
        return BaseResponse.created(response);
    }

}
