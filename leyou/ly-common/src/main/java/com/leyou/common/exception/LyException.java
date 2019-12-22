package com.leyou.common.exception;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Getter;

/**
 * 自定义的异常
 */
@Getter
public class LyException extends RuntimeException {
//    状态码
    private Integer status;

    public LyException(Integer status,String message) {
        super(message);
        this.status = status;
    }

    public LyException(Integer status,String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public LyException(ExceptionEnum enums) {
        super(enums.getMessage());
        this.status = enums.getStatus();
    }
}
