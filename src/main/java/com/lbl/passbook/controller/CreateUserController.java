package com.lbl.passbook.controller;

import com.lbl.passbook.log.LogConstants;
import com.lbl.passbook.log.LogGenerator;
import com.lbl.passbook.service.IUserService;
import com.lbl.passbook.vo.Response;
import com.lbl.passbook.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>创建用户服务</h1>
 * Created by Qinyi.
 */
@Slf4j
@RestController
@RequestMapping("/passbook")
public class CreateUserController {

    /** 创建用户服务 */
    private final IUserService userService;

    /** HttpServletRequest */
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public CreateUserController(IUserService userService,
                                HttpServletRequest httpServletRequest) {
        this.userService = userService;
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * <h2>创建用户</h2>
     * @param user {@link User}
     * @return {@link Response}
     * */
    @ResponseBody
    @PostMapping("/createuser")
    Response createUser(@RequestBody User user) throws Exception {

        LogGenerator.genLog(
                httpServletRequest,
                LogConstants.ActionName.CREATE_USER,
                -1L,
                user
        );
        return userService.createUser(user);
    }
}
