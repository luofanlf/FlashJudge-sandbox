package com.luofan.flashjudge_codesandbox;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.luofan.flashjudge_codesandbox.model.ExecuteCodeRequest;
import com.luofan.flashjudge_codesandbox.model.ExecuteCodeResponse;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;

public class JavaNativeSandBox implements CodeSandBox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";
    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    public static void main(String[] args) {
        JavaNativeSandBox javaNativeSandBox = new JavaNativeSandBox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInput(Arrays.asList("1", "2"));
        String code = ResourceUtil.readUtf8Str("testcode/simpleComputeArgs/Main.java");
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        ExecuteCodeResponse executeCodeResponse = javaNativeSandBox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }
    
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> input = executeCodeRequest.getInput();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;

        //判断全局代码目录是否存在，不存在则创建
        if(!FileUtil.exist(globalCodePathName)){
            FileUtil.mkdir(globalCodePathName);
        }

        //存放用户代码
        String userCodePathName = globalCodePathName + File.separator + UUID.randomUUID().toString();
        String userCodeFilePathName = userCodePathName + File.separator + GLOBAL_JAVA_CLASS_NAME;
        FileUtil.writeUtf8String(code, userCodeFilePathName);

        return null;
    }
}
