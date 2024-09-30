package com.d201.goodshot.expert.controller;

import com.d201.goodshot.expert.dto.ExpertResponse;
import com.d201.goodshot.expert.dto.ExpertResponse.ExpertDetailItem;
import com.d201.goodshot.expert.dto.ExpertResponse.ExpertItemResponse;
import com.d201.goodshot.expert.service.ExpertService;
import com.d201.goodshot.global.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/experts")
@Tag(name = "Expert")
@RequiredArgsConstructor
@Slf4j
public class ExpertController {

    private final ExpertService expertService;

    @GetMapping()
    @Operation(summary = "전문가 목록 조회", description = "pageNo : 현재 페이지, pageSize : 요청하고자 하는 페이지 개수")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "전문가 목록 조회를 성공했습니다.",
                    content = @Content(mediaType = "",
                            examples = @ExampleObject(value = "")))
    })
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<ExpertItemResponse> getExpertList(@RequestParam(value = "pageNo") int pageNo, @RequestParam(value = "pageSize") int pageSize) {
        ExpertItemResponse expertItemResponse = expertService.getExpertList(pageNo, pageSize);
        return BaseResponse.of(HttpStatus.OK, "전문가 목록 조회를 성공했습니다.", expertItemResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "전문가 상세 목록 조회", description = "")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "전문가 상세 목록 조회를 성공했습니다.",
                    content = @Content(mediaType = "",
                            examples = @ExampleObject(value = "")))
    })
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<ExpertDetailItem> getExpertDetail(@PathVariable int id) {
        ExpertDetailItem expertDetailItem = expertService.getExpertDetail(id);
        return BaseResponse.of(HttpStatus.OK, "전문가 상세 목록 조회를 성공했습니다.", expertDetailItem);
    }

}
