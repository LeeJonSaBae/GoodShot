package com.d201.goodshot.swing.controller;

import com.d201.goodshot.global.base.BaseResponse;
import com.d201.goodshot.global.security.dto.CustomUser;
import com.d201.goodshot.swing.dto.SwingRequest;
import com.d201.goodshot.swing.dto.SwingRequest.SwingDataRequest;
import com.d201.goodshot.swing.dto.SwingResponse;
import com.d201.goodshot.swing.dto.SwingResponse.SwingDataResponse;
import com.d201.goodshot.swing.service.SwingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/swings")
@Tag(name = "Swing")
@RequiredArgsConstructor
public class SwingController {

    private final SwingService swingService;

    @PostMapping()
    @Operation(summary = "스윙 내보내기", description = "Room 에 저장되어 있는 데이터 전부 내보내기")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "스윙 데이터 내보내기에 성공했습니다.",
                    content = @Content(mediaType = "",
                            examples = @ExampleObject(value = "")))
    })
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<Void> postSwingData(@AuthenticationPrincipal CustomUser customUser) {
        swingService.postSwingData(customUser);
        return BaseResponse.of(HttpStatus.OK, "스윙 데이터 내보내기에 성공했습니다.", null);
    }

    @GetMapping()
    @Operation(summary = "스윙 가져오기", description = "DB 에 저장되어 있는 데이터 가져오기")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "스윙 데이터 가져오기에 성공했습니다.",
                    content = @Content(mediaType = "",
                            examples = @ExampleObject(value = "")))
    })
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<List<SwingDataResponse>> getSwingData(@AuthenticationPrincipal CustomUser customUser) {
        List<SwingDataResponse> swingDataList = swingService.getSwingData(customUser);
        return BaseResponse.of(HttpStatus.OK, "스윙 데이터 가져오기에 성공했습니다.", swingDataList);
    }

}
