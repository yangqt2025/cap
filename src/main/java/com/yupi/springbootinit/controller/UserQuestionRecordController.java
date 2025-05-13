package com.yupi.springbootinit.controller;

import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.model.dto.userquestionrecord.UserQuestionRecordAddRequest;
import com.yupi.springbootinit.model.dto.userquestionrecord.UserQuestionRecordQueryRequest;
import com.yupi.springbootinit.model.vo.UserQuestionRecordVO;
import com.yupi.springbootinit.service.UserQuestionRecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户做题记录接口
 */
@RestController
@RequestMapping("/userQuestionRecord")
public class UserQuestionRecordController {

    @Resource
    private UserQuestionRecordService userQuestionRecordService;

    /**
     * 添加做题记录
     *
     * @param userQuestionRecordAddRequest 添加请求
     * @return 记录ID
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUserQuestionRecord(@RequestBody UserQuestionRecordAddRequest userQuestionRecordAddRequest) {
        Long recordId = userQuestionRecordService.addUserQuestionRecord(userQuestionRecordAddRequest);
        return ResultUtils.success(recordId);
    }

    /**
     * 获取用户做题记录列表
     *
     * @param userQuestionRecordQueryRequest 查询请求
     * @return 做题记录列表
     */
    @PostMapping("/list")
    public BaseResponse<List<UserQuestionRecordVO>> listUserQuestionRecords(@RequestBody UserQuestionRecordQueryRequest userQuestionRecordQueryRequest) {
        List<UserQuestionRecordVO> userQuestionRecordVOList = userQuestionRecordService.listUserQuestionRecords(userQuestionRecordQueryRequest);
        return ResultUtils.success(userQuestionRecordVOList);
    }

    /**
     * 获取用户做题记录
     *
     * @param id 记录ID
     * @return 做题记录
     */
    @GetMapping("/get")
    public BaseResponse<UserQuestionRecordVO> getUserQuestionRecordById(@RequestParam Long id) {
        UserQuestionRecordVO userQuestionRecordVO = userQuestionRecordService.getUserQuestionRecordById(id);
        return ResultUtils.success(userQuestionRecordVO);
    }
} 