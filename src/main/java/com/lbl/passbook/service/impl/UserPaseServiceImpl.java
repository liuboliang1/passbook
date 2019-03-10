package com.lbl.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.lbl.passbook.constant.Constants;
import com.lbl.passbook.constant.PassStatus;
import com.lbl.passbook.dao.MerchantsDao;
import com.lbl.passbook.entity.Merchants;
import com.lbl.passbook.mapper.PassRowMapper;
import com.lbl.passbook.service.IUserPassService;
import com.lbl.passbook.vo.Pass;
import com.lbl.passbook.vo.PassInfo;
import com.lbl.passbook.vo.PassTemplate;
import com.lbl.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserPaseServiceImpl implements IUserPassService {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Autowired
    private MerchantsDao merchantsDao;



    @Override
    public Response getUserPassInfo(Long userId) throws Exception {

        return getPassInfoByStatus(userId, PassStatus.UNUSED);
    }

    @Override
    public Response getUserUsedPassInfo(Long userId) throws Exception {

        return getPassInfoByStatus(userId, PassStatus.USED);
    }

    @Override
    public Response getUserAllPassInfo(Long userId) throws Exception {

        return getPassInfoByStatus(userId, PassStatus.ALL);
    }

    @Override
    public Response userUsePass(Pass pass) {
        byte[] rowPrefix = Bytes.toBytes(
                new StringBuilder(String.valueOf(pass.getUserId())).reverse().toString()
        );

        Scan scan = new Scan();
        List<Filter> filters = new ArrayList<>();
        filters.add(new ColumnPrefixFilter(rowPrefix));

        filters.add(new SingleColumnValueFilter(
                Constants.PassTable.FAMILY_I.getBytes(),
                Constants.PassTable.TEMPLATE_ID.getBytes(),
                CompareFilter.CompareOp.EQUAL,
                Bytes.toBytes(pass.getTemplateId())
        ));

        filters.add(new SingleColumnValueFilter(
                Constants.PassTable.FAMILY_I.getBytes(),
                Constants.PassTable.CON_DATE.getBytes(),
                CompareFilter.CompareOp.EQUAL,
                Bytes.toBytes("-1")
        ));

        scan.setFilter(new FilterList(filters));

        List<Pass> passList = hbaseTemplate.find(Constants.PassTable.TABLE_NAME, scan, new PassRowMapper());

        if (null == passList || passList.size() != 1) {
            log.error("UserUsePass Error: {}", JSON.toJSONString(pass));
            return Response.failure("UserUsePass Error");
        }

        List<Mutation> datas = new ArrayList<>();
        Put put = new Put(passList.get(0).getRowKey().getBytes());
        put.addColumn(
                Constants.PassTable.FAMILY_I.getBytes(),
                Constants.PassTable.CON_DATE.getBytes(),
                Bytes.toBytes(DateFormatUtils.ISO_DATE_FORMAT.format(new Date()))
        );
        datas.add(put);
        hbaseTemplate.saveOrUpdates(
                Constants.PassTable.TABLE_NAME, datas
        );
        return Response.success();
    }


    /**
     * 根据状态查询优惠券
     * @param userId
     * @param passStatus
     * @return
     */
    private Response getPassInfoByStatus(Long userId, PassStatus passStatus) throws Exception {

        byte[] rowPrifix = Bytes.toBytes(new StringBuilder().append(userId).reverse().toString());

        CompareFilter.CompareOp compareOp = passStatus == PassStatus.UNUSED
                ? CompareFilter.CompareOp.EQUAL : CompareFilter.CompareOp.NOT_EQUAL;

        Scan scan = new Scan();
        List<Filter> filters = new ArrayList<>();

        filters.add(new ColumnPrefixFilter(rowPrifix));

        if(passStatus != PassStatus.ALL) {
            filters.add(new SingleColumnValueFilter(
                    Constants.PassTable.FAMILY_I.getBytes(),
                    Constants.PassTable.CON_DATE.getBytes(),
                    compareOp,
                    Bytes.toBytes("-1")
            ));
        }
        scan.setFilter(new FilterList(filters));
        List<Pass> passList = hbaseTemplate.find(Constants.PassTable.TABLE_NAME, scan, new PassRowMapper());

        Map<String, PassTemplate> passTemplateMap = buildPassTemplate2Map(passList);
        Map<Integer, Merchants> merchantsMap = buildMerchants2Map(new ArrayList<PassTemplate>(passTemplateMap.values()));

        List<PassInfo> passInfos = new ArrayList<>();
        for (Pass pass : passList) {
            PassTemplate passTemplate = passTemplateMap.getOrDefault(pass.getTemplateId(), null);
            if (null == passTemplate) {
                log.error("PassTemplate Null : {}", pass.getTemplateId());
                continue;
            }
            Merchants merchants = merchantsMap.getOrDefault(passTemplate.getId(), null);
            if (null == merchants) {
                log.error("Merchants Null : {}", passTemplate.getId());
                continue;
            }

            passInfos.add(new PassInfo(pass,passTemplate,merchants));
        }
//

        return new Response(passInfos);
    }

    private Map<String, PassTemplate> buildPassTemplate2Map(List<Pass> passList) throws Exception {
        List<String> templateIds = passList.stream()
                .map(Pass::getTemplateId).collect(Collectors.toList());

        List<Get> templateGets = new ArrayList<>(templateIds.size());
        templateIds.stream().forEach(t -> templateGets.add(new Get(Bytes.toBytes(t))));
        Result[] results = hbaseTemplate.getConnection()
                .getTable(TableName.valueOf(Constants.PassTemplateTable.TABLE_NAME))
                .get(templateGets);

        String[] patterns = new String[] {"yyyy-MM-dd"};

        byte[] FAMILY_B = Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B);
        byte[] ID = Bytes.toBytes(Constants.PassTemplateTable.ID);
        byte[] TITLE = Bytes.toBytes(Constants.PassTemplateTable.TITLE);
        byte[] SUMMARY = Bytes.toBytes(Constants.PassTemplateTable.SUMMARY);
        byte[] DESC = Bytes.toBytes(Constants.PassTemplateTable.DESC);
        byte[] HAS_TOKEN = Bytes.toBytes(Constants.PassTemplateTable.HAS_TOKEN);
        byte[] BACKGROUND = Bytes.toBytes(Constants.PassTemplateTable.BACKGROUND);

        byte[] FAMILY_C = Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C);
        byte[] LIMIT = Bytes.toBytes(Constants.PassTemplateTable.LIMIT);
        byte[] START = Bytes.toBytes(Constants.PassTemplateTable.START);
        byte[] END = Bytes.toBytes(Constants.PassTemplateTable.END);

        Map<String, PassTemplate> passTemplateMap = new HashMap<>();
        for (Result result : results) {
            PassTemplate passTemplate = new PassTemplate();

            passTemplate.setId(Bytes.toInt(result.getValue(FAMILY_B, ID)));
            passTemplate.setTitle(Bytes.toString(result.getValue(FAMILY_B, TITLE)));
            passTemplate.setSummary(Bytes.toString(result.getValue(FAMILY_B, SUMMARY)));
            passTemplate.setDesc(Bytes.toString(result.getValue(FAMILY_B, DESC)));
            passTemplate.setHasToken(Bytes.toBoolean(result.getValue(FAMILY_B, HAS_TOKEN)));
            passTemplate.setBackground(Bytes.toInt(result.getValue(FAMILY_B, BACKGROUND)));

            passTemplate.setLimit(Bytes.toLong(result.getValue(FAMILY_C, LIMIT)));
            passTemplate.setStart(DateUtils.parseDate(Bytes.toString(result.getValue(FAMILY_C, START)), patterns));
            passTemplate.setEnd(DateUtils.parseDate(Bytes.toString(result.getValue(FAMILY_C, END)), patterns));

            passTemplateMap.put(Bytes.toString(result.getRow()), passTemplate);
        }

        return passTemplateMap;
    }


    private Map<Integer, Merchants> buildMerchants2Map(List<PassTemplate> passTemplateList) {
        List<Integer> merchantsIds = passTemplateList.stream()
                .map(PassTemplate::getId).collect(Collectors.toList());

        List<Merchants> merchants = merchantsDao.findByIdIn(merchantsIds);

        Map<Integer, Merchants> merchantsMap = new HashMap<>();

        merchants.stream().forEach(t -> merchantsMap.put(t.getId(), t));

        return merchantsMap;
    }


}
