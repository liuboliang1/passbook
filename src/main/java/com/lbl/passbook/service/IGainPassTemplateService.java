package com.lbl.passbook.service;

import com.lbl.passbook.vo.GainPassTemplateRequest;
import com.lbl.passbook.vo.Response;

import java.io.IOException;

/**
 * 用户领取优惠券
 */
public interface IGainPassTemplateService {
    /**
     * 用户领取优惠券
     * @param gainPassTemplateRequest 用户领取优惠券请求对象
     * @return {@link Response}
     */
    Response gainPassTemplate(GainPassTemplateRequest gainPassTemplateRequest) throws IOException;
}
