package com.lbl.passbook.utils;

import com.lbl.passbook.vo.Feedback;
import com.lbl.passbook.vo.GainPassTemplateRequest;
import com.lbl.passbook.vo.PassTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * RowKey生成器工具类
 */
@Slf4j
public class RowKeyGenUtil {

    /**
     * 根据提供的 PassTemplate 对象生成 RowKey
     * 生成规则：id+"_"+title
     * @param passTemplate
     * @return
     */
    public static String genPassTemplateRowKey(PassTemplate passTemplate) {
        String passInfo = new StringBuilder(passTemplate.getId()).append("_").append(passTemplate.getTitle()).toString();
        String rowKey = DigestUtils.md5Hex(passInfo);
        log.info("GenPassTemplateRowKey: {}, {}", passTemplate, rowKey);
        return rowKey;
    }

    /**
     * 根据提供的 GainPassTemplateRequest 对象生成 RowKey
     * 生成规则：Pass RowKey = reversed(userId) + inverse(timestamp) + PassTemplate RowKey
     * @param gainPassTemplateRequest
     * @return
     */
    public static String genPassRowKey(GainPassTemplateRequest gainPassTemplateRequest) {
        String rowKey = new StringBuilder(gainPassTemplateRequest.getUserId()).reverse()
                .append(Long.MAX_VALUE - System.currentTimeMillis())
                .append(genPassTemplateRowKey(gainPassTemplateRequest.getPassTemplate())).toString();
        log.info("GenPassRowKey: {}, {}",gainPassTemplateRequest, rowKey);
        return rowKey;
    }

    /**
     * 根据提供的 Feedback 对象生成 RowKey
     * 生成规则：userId.reverse + (Long.MAX_VALUE - System.currentTimeMillis())
     * @param feedback
     * @return
     */
    public static String genFeedbackRowKey(Feedback feedback) {
         String rowKey = new StringBuilder(String.valueOf(feedback.getUserId())).reverse()
                .append(Long.MAX_VALUE - System.currentTimeMillis()).toString();
         log.info("GenFeedbackRowKey: {}, {}",feedback, rowKey);
         return rowKey;
    }

}
