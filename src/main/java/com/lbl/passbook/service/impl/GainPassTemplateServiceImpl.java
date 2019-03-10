package com.lbl.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.lbl.passbook.constant.Constants;
import com.lbl.passbook.mapper.PassTemplateRowMapper;
import com.lbl.passbook.service.IGainPassTemplateService;
import com.lbl.passbook.utils.RowKeyGenUtil;
import com.lbl.passbook.vo.GainPassTemplateRequest;
import com.lbl.passbook.vo.PassTemplate;
import com.lbl.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

@Service
@Slf4j
public class GainPassTemplateServiceImpl implements IGainPassTemplateService {

    private final HbaseTemplate hbaseTemplate;

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public GainPassTemplateServiceImpl(HbaseTemplate hbaseTemplate, StringRedisTemplate redisTemplate) {
        this.hbaseTemplate = hbaseTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Response gainPassTemplate(GainPassTemplateRequest request) throws IOException {
        String passTemplateId = RowKeyGenUtil.genPassTemplateRowKey(request.getPassTemplate());
//        判断优惠券是否存在
        PassTemplate passTemplate = null;
        try {
//          如果该优惠券模板不存在，get方法会报错
            passTemplate = hbaseTemplate.get(
                    Constants.PassTemplateTable.TABLE_NAME,
                    passTemplateId,
                    new PassTemplateRowMapper()
            );
        } catch (Exception e) {
            log.error("Gain PassTemplate Error: {}",
                    JSON.toJSONString(request.getPassTemplate()));
            return Response.failure("Gain PassTemplate Error!");
        }
        //判断优惠券的领取有没有达到上限
        if(passTemplate.getLimit()<1 && passTemplate.getLimit()!=-1) {
            log.error("PassTemplate Limit Max: {}",
                    JSON.toJSONString(request.getPassTemplate()));
            return Response.failure("PassTemplate Limit Max!");
        }

        //判断优惠券的领取日期是否过期
        long currentTimeMillis = System.currentTimeMillis();
        if(!(currentTimeMillis > passTemplate.getStart().getTime()
                && currentTimeMillis < passTemplate.getEnd().getTime())) {
            log.error("PassTemplate ValidTime Error: {}",
                    JSON.toJSONString(request.getPassTemplate()));
            return Response.failure("PassTemplate ValidTime Error!");
        }

        //优惠券的库存减一
        if(passTemplate.getLimit() != -1) {

            List<Mutation> datas = new ArrayList<>();
            Put put = new Put(Bytes.toBytes(passTemplateId));
            put.addColumn(
                    Constants.PassTemplateTable.FAMILY_C.getBytes(),
                    Constants.PassTemplateTable.LIMIT.getBytes(),
                    Bytes.toBytes(passTemplate.getLimit() - 1)
            );
            datas.add(put);
            hbaseTemplate.saveOrUpdates(Constants.PassTemplateTable.TABLE_NAME, datas);
        }
        //将领取的优惠券信息保存到pass表
        if (!addPassForUser(request, passTemplate.getId(), passTemplateId)) {
            return Response.failure("GainPassTemplate Failure!");
        }
        return new Response();
    }

    /**
     * 将领取的优惠券信息保存到pass表
     * @param request
     * @param merchantsId
     * @param passTemplateId
     * @return
     */
    private boolean addPassForUser(GainPassTemplateRequest request, Integer merchantsId, String passTemplateId) throws IOException {
        byte[] FAMILY_I = Bytes.toBytes(Constants.PassTable.FAMILY_I);
        byte[] USER_ID = Bytes.toBytes(Constants.PassTable.USER_ID);
        byte[] TEMPLATE_ID = Bytes.toBytes(Constants.PassTable.TEMPLATE_ID);
        byte[] TOKEN = Bytes.toBytes(Constants.PassTable.TOKEN);
        byte[] ASSIGNED_DATE = Bytes.toBytes(Constants.PassTable.ASSIGNED_DATE);
        byte[] CON_DATE = Bytes.toBytes(Constants.PassTable.CON_DATE);

        List<Mutation> datas = new ArrayList<>();
        String passRowKey = RowKeyGenUtil.genPassRowKey(request);
        Put put = new Put(Bytes.toBytes(passRowKey));
        put.addColumn(FAMILY_I, USER_ID, Bytes.toBytes(request.getUserId()));

        put.addColumn(FAMILY_I, TEMPLATE_ID, Bytes.toBytes(passTemplateId));

        if(request.getPassTemplate().getHasToken()) {
            String token = redisTemplate.opsForSet().pop(passTemplateId);
            if(null == token) {
                log.error("Token not exist: {}", passTemplateId);
                return false;
            }
            log.info("{} User token: {}", passTemplateId, token);
            recordTokenToFile(merchantsId, passTemplateId, token);
            put.addColumn(FAMILY_I, TOKEN, Bytes.toBytes(token));
        } else {
            put.addColumn(FAMILY_I, TOKEN, Bytes.toBytes("-1"));
        }

        put.addColumn(FAMILY_I, ASSIGNED_DATE, Bytes.toBytes(DateFormatUtils.ISO_DATE_FORMAT.format(new Date())));
        put.addColumn(FAMILY_I, CON_DATE, Bytes.toBytes("-1"));
        datas.add(put);
        hbaseTemplate.saveOrUpdates(Constants.PassTable.TABLE_NAME, datas);
        return true;
    }

    /**
     * <h2>将已使用的 token 记录到文件中</h2>
     * @param merchantsId 商户 id
     * @param passTemplateId 优惠券 id
     * @param token 分配的优惠券 token
     * */
    private void recordTokenToFile(Integer merchantsId, String passTemplateId, String token) throws IOException {
        Files.write(
                Paths.get(Constants.TOKEN_DIR, String.valueOf(merchantsId),
                        passTemplateId + Constants.USED_TOKEN_SUFFIX),
                (token + "\n").getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND
        );
    }

}
