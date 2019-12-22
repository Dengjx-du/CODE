package com.leyou.common.advice;

import com.leyou.common.exception.LyException;
import com.leyou.common.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 环绕通知类
 */
@ControllerAdvice  //对所有的controller抛出的异常进行环绕通知
public class BasicExceptionAdvice {

    /**
     * 处理lyexception异常
     * @param e
     */
    @ExceptionHandler(value = LyException.class)
    public ResponseEntity<ExceptionResult> lyExceptionHandler(LyException e){
        return ResponseEntity.status(e.getStatus()).body(new ExceptionResult(e));
    }
}
