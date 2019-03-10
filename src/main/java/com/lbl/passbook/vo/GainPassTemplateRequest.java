package com.lbl.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户领取优惠券请求对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GainPassTemplateRequest {

    /** 用户id */
    private Long userId;

    /** 优惠券模板信息 */
    private PassTemplate passTemplate;
}
