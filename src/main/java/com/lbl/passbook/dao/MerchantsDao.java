package com.lbl.passbook.dao;

import com.lbl.passbook.entity.Merchants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchantsDao extends JpaRepository<Merchants, Integer> {

    /**
     * 根据商户名称查询
     * @param name
     * @return
     */
    Merchants findByName(String name);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    Merchants findById(Integer id);

    /**
     * 根据ids获取商户信息
     * @param ids
     * @return
     */
    List<Merchants> findByIdIn(List<Integer> ids);


}
