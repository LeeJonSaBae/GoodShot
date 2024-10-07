package com.d201.goodshot.swing.controller;

import com.d201.goodshot.global.base.BaseResponse;
import com.d201.goodshot.global.security.dto.CustomUser;
import com.d201.goodshot.swing.domain.Swing;
import com.d201.goodshot.swing.dto.SwingData;
import com.d201.goodshot.swing.dto.SwingRequest;
import com.d201.goodshot.swing.dto.SwingRequest.SwingDataRequest;
import com.d201.goodshot.swing.dto.SwingRequest.SwingUpdateDataRequest;
import com.d201.goodshot.swing.dto.SwingResponse;
import com.d201.goodshot.swing.dto.SwingResponse.ReportResponse;
import com.d201.goodshot.swing.dto.SwingResponse.SwingCodeResponse;
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

import java.util.List;

@RestController
@RequestMapping("/swings")
@Tag(name = "Swing")
@RequiredArgsConstructor
public class SwingController {

    private final SwingService swingService;

    @GetMapping("/comparison")
    @Operation(summary = "스윙 비교하기", description = "Room 에 저장되어 있는 데이터랑 DB 에 저장되어 있는 데이터 비교해서 서버에 없는 데이터 보내기")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "스윙 데이터 비교하기에 성공했습니다.",
                    content = @Content(mediaType = "",
                            examples = @ExampleObject(value = "")))
    })
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<List<SwingCodeResponse>> compareSwingData(@AuthenticationPrincipal CustomUser customUser, @RequestBody SwingDataRequest swingDataRequest) {
        List<SwingCodeResponse> response = swingService.compareSwingData(customUser, swingDataRequest);
        return BaseResponse.of(HttpStatus.OK, "스윙 데이터 비교하기에 성공했습니다.", response);
    }

    @PostMapping("/export")
    @Operation(summary = "스윙 내보내기", description = "Room 에 저장되어 있는 데이터 전부 내보내기")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "스윙 데이터 내보내기에 성공했습니다.",
                    content = @Content(mediaType = "",
                            examples = @ExampleObject(value = "")))
    })
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<Void> exportSwingData(@AuthenticationPrincipal CustomUser customUser, @RequestBody List<SwingData> swingDataList) {
        swingService.exportSwingData(customUser, swingDataList);
        return BaseResponse.of(HttpStatus.OK, "스윙 데이터 내보내기에 성공했습니다.", null);
    }

    @GetMapping("/import")
    @Operation(summary = "스윙 가져오기", description = "DB 에 저장되어 있는 데이터 가져오기")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "스윙 데이터 가져오기에 성공했습니다.",
                    content = @Content(mediaType = "",
                            examples = @ExampleObject(value = "")))
    })
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<List<SwingData>> importSwingData(@AuthenticationPrincipal CustomUser customUser, @RequestBody SwingDataRequest swingDataRequest) {
        List<SwingData> response = swingService.importSwingData(customUser, swingDataRequest);
        return BaseResponse.of(HttpStatus.OK, "스윙 데이터 가져오기에 성공했습니다.", response);
    }

    @GetMapping("/report")
    @Operation(summary = "스윙 종합 리포트", description = "누적 스윙에 대한 종합 리포트 제공")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "스윙 종합 리포트 가져오기에 성공했습니다.",
                    content = @Content(mediaType = "",
                            examples = @ExampleObject(value = "")))
    })
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<ReportResponse> getReport(@AuthenticationPrincipal CustomUser customUser) {
        ReportResponse response = swingService.getReport(customUser);
        return BaseResponse.of(HttpStatus.OK, "스윙 종합 리포트 가져오기에 성공했습니다.", response);
    }

    @PostMapping("/sync")
    @Operation(summary = "스윙 동기화", description = "변경된 사항 서버에 적용하기")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "스윙 동기화에 성공했습니다.",
                    content = @Content(mediaType = "",
                            examples = @ExampleObject(value = "")))
    })
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<Void> syncSwingData(@AuthenticationPrincipal CustomUser customUser, @RequestBody List<SwingUpdateDataRequest> swingUpdateDataRequestList) {
        swingService.syncSwingData(customUser, swingUpdateDataRequestList);
        return BaseResponse.of(HttpStatus.OK, "스윙 동기화에 성공했습니다.", null);
    }

}
