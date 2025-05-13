package com.yupi.springbootinit.controller;

import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.model.vo.EssayRecordVO;
import com.yupi.springbootinit.service.EssayService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/essay")
public class EssayController {

    private final EssayService essayService;

    public EssayController(EssayService essayService) {
        this.essayService = essayService;
    }

    /**
     * 获取答题记录详情
     * @param recordId 答题记录ID
     * @return 答题记录详情
     */
    @GetMapping("/record/{recordId}")
    public BaseResponse<EssayRecordVO> getRecordDetail(@PathVariable Long recordId) {
        EssayRecordVO recordDetail = essayService.getRecordDetail(recordId);
        return ResultUtils.success(recordDetail);
    }
} 