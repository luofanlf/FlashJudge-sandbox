package com.luofan.flashjudge_codesandbox;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


import com.luofan.flashjudge_codesandbox.model.ExecuteCodeRequest;
import com.luofan.flashjudge_codesandbox.model.ExecuteCodeResponse;
import com.luofan.flashjudge_codesandbox.model.ExecuteMessage;
import com.luofan.flashjudge_codesandbox.model.JudgeInfo;
import com.luofan.flashjudge_codesandbox.utils.ProcessUtils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;

public class JavaNativeSandBox implements CodeSandBox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";
    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    private static final long TIME_OUT = 30000L;

    private static final List<String> BLACK_LIST = Arrays.asList("Files","exec");

    private static final WordTree WORD_TREE;

    private static final String SECURITY_MANAGER_PATH = "/Users/luofan/code/flashoj-codesandbox/src/main/resources/security";
    
    //初始化wordtree来匹配黑名单中的命令
    static{
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(BLACK_LIST);
    }

    public static void main(String[] args) {
        JavaNativeSandBox javaNativeSandBox = new JavaNativeSandBox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInput(Arrays.asList("1 2", "2 3"));
        String code = ResourceUtil.readUtf8Str("testcode/execTest/Main.java");
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        ExecuteCodeResponse executeCodeResponse = javaNativeSandBox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }
    
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInput();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;

        //校验代码
        FoundWord foundWord = WORD_TREE.matchWord(code);
        if(foundWord!=null){
            System.out.println("检测到危险代码: " + foundWord.getFoundWord());
            ExecuteCodeResponse errorResponse = new ExecuteCodeResponse();
            errorResponse.setMessage("代码包含危险操作: " + foundWord.getFoundWord());
            errorResponse.setOutput(new ArrayList<>());
            errorResponse.setStatus("fail");
            errorResponse.setJudgeInfo(new JudgeInfo());
            return errorResponse;
        }


        //判断全局代码目录是否存在，不存在则创建
        if(!FileUtil.exist(globalCodePathName)){
            FileUtil.mkdir(globalCodePathName);
        }

        //1.存放用户代码
        String userCodePathName = globalCodePathName + File.separator + UUID.randomUUID().toString();
        String userCodeFilePathName = userCodePathName + File.separator + GLOBAL_JAVA_CLASS_NAME;
        File userCodeFile = FileUtil.writeUtf8String(code, userCodeFilePathName);

        //2.编译用户代码
        String compileCmd = String.format("javac -encoding UTF-8 %s", userCodeFile.getAbsolutePath());
        try {
            Process process = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage message = ProcessUtils.runProcess(process, "编译");
            System.out.println(message);
        } catch (Exception e) {
            return getErrorResponse(e);
        }
        //3.运行用户代码
        List<ExecuteMessage> messageList = new ArrayList<>();
        for(String inputArgs : inputList){

            String runCmd = String.format("java -Xmx256m -Djava.security.manager=MySecurityManager -cp %s:%s Main %s", userCodePathName, SECURITY_MANAGER_PATH, inputArgs);
            try {
                Process process = Runtime.getRuntime().exec(runCmd);
                // start a new thread to execute timeout
                Thread timeoutThread = new Thread(() -> {
                    try{
                        Thread.sleep(TIME_OUT);
                        System.out.println("time out,interrupt the execution");
                        process.destroyForcibly();
                    }catch(InterruptedException e){
          
                    }    
                });
                timeoutThread.start();
                ExecuteMessage message = ProcessUtils.runProcess(process, "执行");
                System.out.println(message);
                messageList.add(message);
            }catch (Exception e) {
                return getErrorResponse(e);
            }
        }
        //4.整理输出
        List<String> outputList = new ArrayList<>();
        long maxTime = 0;
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        for(ExecuteMessage message:messageList){
            if(StrUtil.isNotBlank(message.getErrorMessage())){
                executeCodeResponse.setMessage(message.getErrorMessage());
                executeCodeResponse.setStatus("fail");
                break;
            }
            if(message.getTime()!=null){
                maxTime = Math.max(maxTime,message.getTime());
            }
            outputList.add(message.getMessage());
        }
        if(outputList.size()==messageList.size()){
            executeCodeResponse.setStatus("accepted");
        }
        executeCodeResponse.setOutput(outputList);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);
        judgeInfo.setMemory(null);
        executeCodeResponse.setJudgeInfo(judgeInfo);

        //5.文件清理
        if(userCodeFile.getParentFile() != null){
            boolean res = FileUtil.del(userCodePathName);
            System.out.println("删除"+ (res ? "成功":"失败"));
        }

        return executeCodeResponse;
    }

    /**
     * 获取错误响应
     * @param e
     * @return
     */
    private ExecuteCodeResponse getErrorResponse(Throwable e){
        //构建错误响应
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setMessage(e.getMessage());
        executeCodeResponse.setOutput(new ArrayList<>());
        executeCodeResponse.setStatus("externel error");
        executeCodeResponse.setJudgeInfo(new JudgeInfo());

        return executeCodeResponse;
    }
}
