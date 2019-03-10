package com.lbl.passbook.advice;

import com.lbl.passbook.vo.ErrorInfo;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常统一处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ErrorInfo errorHandler(HttpServletRequest request, Exception ex) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setCode(ErrorInfo.ERROR);
        errorInfo.setMessage(ex.getMessage());
        errorInfo.setData("Do Not Have Return Data");
        errorInfo.setUrl(request.getRequestURL().toString());
        return errorInfo;
    }


}
