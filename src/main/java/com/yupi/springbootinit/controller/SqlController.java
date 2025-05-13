package com.yupi.springbootinit.controller;

import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.utils.SqlUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sql")
public class SqlController {

    @Resource
    private SqlUtils sqlUtils;

    @PostMapping("/execute")
    public BaseResponse<Boolean> executeSql() {
        sqlUtils.executeSqlFile("sql/alter_user_table.sql");
        return ResultUtils.success(true);
    }
} 