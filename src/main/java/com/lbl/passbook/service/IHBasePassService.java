package com.lbl.passbook.service;

import com.lbl.passbook.vo.PassTemplate;

/**
 * Pass Hbase 服务
 */
public interface IHBasePassService {

    /**
     * <h2>将 PassTemplate 写入 HBase</h2>
     * @param passTemplate {@link PassTemplate}
     * @return true/false
     * */
    boolean dropPassTemplateToHBase(PassTemplate passTemplate);

}
