package com.luofan.flashjudge_codesandbox.model;

import java.util.List;


import lombok.Data;

@Data
public class ExecuteCodeResponse {
    /**
     * 输出
     */
    private List<String> output;

    /**
     * 接口信息
     */
    private String message;

    /**
     * 状态
     */
    private String status;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

}
