package com.lbl.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一错误处理
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorInfo<T> {
    /** 错误码 */
    public static final Integer ERROR = -1;

    /** 特定错误码 */
    private Integer code;

    /** 错误信息 */
    private String message;

    /** 请求url */
    private String url;

    /** 返回数据 */
    private T data;


}
