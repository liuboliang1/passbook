package com.lbl.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    /** 响应代码 成功为0 */
    private Integer errCode = 0;
    /** 响应信息 成功为空字符串 "" */
    private String errMsg = "";
    /** 响应数据 */
    private Object data;

    /**
     * 正确的响应构造函数
     * @param data
     */
    public Response(Object data) {
        this.data = data;
    }

    /**
     * 空响应
     * @return
     */
    public static Response success() {
        return new Response();
    }

    /**
     * 失败的响应
     * @param errMsg
     * @return
     */
    public static Response failure(String errMsg) {
        return new Response(-1, errMsg, null);
    }

}
