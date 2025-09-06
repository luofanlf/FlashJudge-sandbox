package com.luofan.flashjudge_codesandbox;

import com.luofan.flashjudge_codesandbox.model.ExecuteCodeRequest;
import com.luofan.flashjudge_codesandbox.model.ExecuteCodeResponse;

public interface CodeSandBox {

    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
