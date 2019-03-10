package com.lbl.passbook.service;

import com.lbl.passbook.vo.Response;
import com.lbl.passbook.vo.User;

/**
 * 用户服务
 */
public interface IUserService {

    /**
     * 创建用户
     * @param user
     * @return
     * @throws Exception
     */
    Response createUser(User user) throws Exception;

}
