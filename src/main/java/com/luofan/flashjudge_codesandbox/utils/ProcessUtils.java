package com.luofan.flashjudge_codesandbox.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.luofan.flashjudge_codesandbox.model.ExecuteMessage;

/**
 * 进程工具类
 */
public class ProcessUtils {
    
    /**
     * 执行进程并获取信息
     * @param process
     * @param opName
     * @return
     */
    public static ExecuteMessage runProcess(Process process,String opName){
        ExecuteMessage message = new ExecuteMessage();
        try{
            int exitValue = process.waitFor();
            message.setExitValue(exitValue);
            //正常退出
            if(exitValue == 0){
                System.out.println(opName+"成功");
                //分批获取控制台正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                //逐行读取控制台正常输出
                String line;
                while((line = bufferedReader.readLine()) != null){
                    output.append(line);
                }
                message.setMessage(output.toString());
            }else{
                System.out.println(opName+"失败");
                //分批获取控制台正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                //逐行读取控制台正常输出
                String line;
                while((line = bufferedReader.readLine()) != null){
                    output.append(line);
                }
                //分批获取控制台错误输出
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                StringBuilder errorOutput = new StringBuilder();
                //逐行读取控制台错误输出
                String errorLine;
                while((errorLine = errorBufferedReader.readLine()) != null){
                    errorOutput.append(errorLine);
                }
                message.setMessage(output.toString());
                message.setErrorMessage(errorOutput.toString());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return message;
    }
}
