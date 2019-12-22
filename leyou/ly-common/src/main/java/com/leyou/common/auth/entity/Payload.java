package com.leyou.common.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 载荷对象
 * @param <T>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payload<T> {

    /**
     * jwt的唯一标识
     */
    private String id;
    private T userInfo;
    private Date expiration;
}
