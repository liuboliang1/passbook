package com.lbl.passbook.controller;

import com.lbl.passbook.log.LogConstants;
import com.lbl.passbook.log.LogGenerator;
import com.lbl.passbook.service.IFeedbackService;
import com.lbl.passbook.service.IGainPassTemplateService;
import com.lbl.passbook.service.IInventoryService;
import com.lbl.passbook.service.IUserPassService;
import com.lbl.passbook.vo.Feedback;
import com.lbl.passbook.vo.GainPassTemplateRequest;
import com.lbl.passbook.vo.Pass;
import com.lbl.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>Passbook Rest Controller</h1>
 * Created by Qinyi.
 */
@Slf4j
@RestController
@RequestMapping("/passbook")
public class PassbookController {

    /** 用户优惠券服务 */
    private final IUserPassService userPassService;

    /** 优惠券库存服务 */
    private final IInventoryService inventoryService;

    /** 领取优惠券服务 */
    private final IGainPassTemplateService gainPassTemplateService;

    /** 反馈服务 */
    private final IFeedbackService feedbackService;

    /** HttpServletRequest */
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public PassbookController(IUserPassService userPassService,
                              IInventoryService inventoryService,
                              IGainPassTemplateService gainPassTemplateService,
                              IFeedbackService feedbackService,
                              HttpServletRequest httpServletRequest) {
        this.userPassService = userPassService;
        this.inventoryService = inventoryService;
        this.gainPassTemplateService = gainPassTemplateService;
        this.feedbackService = feedbackService;
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * <h2>获取用户个人的优惠券信息</h2>
     * @param userId 用户 id
     * @return {@link Response}
     * */
    @ResponseBody
    @GetMapping("/userpassinfo")
    Response userPassInfo(Long userId) throws Exception {

        LogGenerator.genLog(
                httpServletRequest,
                LogConstants.ActionName.USER_PASS_INFO,
                userId,
                null
        );
        return userPassService.getUserPassInfo(userId);
    }

    /**
     * <h2>获取用户使用了的优惠券信息</h2>
     * @param userId 用户 id
     * @return {@link Response}
     * */
    @ResponseBody
    @GetMapping("userusedpassinfo")
    Response userUsedPassInfo(Long userId) throws Exception {

        LogGenerator.genLog(
                httpServletRequest,
                LogConstants.ActionName.USER_USED_PASS_INFO, userId,
                null
        );
        return userPassService.getUserUsedPassInfo(userId);
    }

    /**
     * <h2>用户使用优惠券</h2>
     * @param pass {@link Pass}
     * @return {@link Response}
     * */
    @ResponseBody
    @PostMapping("/userusepass")
    Response userUsePass(@RequestBody Pass pass) {

        LogGenerator.genLog(
                httpServletRequest,
                LogConstants.ActionName.USER_USE_PASS,
                pass.getUserId(),
                pass
        );
        return userPassService.userUsePass(pass);
    }

    /**
     * <h2>获取库存信息</h2>
     * @param userId 用户 id
     * @return {@link Response}
     * */
    @ResponseBody
    @GetMapping("/inventoryinfo")
    Response inventoryInfo(Long userId) throws Exception {

        LogGenerator.genLog(
                httpServletRequest,
                LogConstants.ActionName.INVENTORY_INFO,
                userId,
                null
        );
        return inventoryService.getInventoryInfo(userId);
    }

    /**
     * <h2>用户领取优惠券</h2>
     * @param request {@link GainPassTemplateRequest}
     * @return {@link Response}
     * */
    @ResponseBody
    @PostMapping("/gainpasstemplate")
    Response gainPassTemplate(@RequestBody GainPassTemplateRequest request)
            throws Exception {

        LogGenerator.genLog(
                httpServletRequest,
                LogConstants.ActionName.GAIN_PASS_TEMPLATE,
                request.getUserId(),
                request
        );
        return gainPassTemplateService.gainPassTemplate(request);
    }

    /**
     * <h2>用户创建评论</h2>
     * @param feedback {@link Feedback}
     * @return {@link Response}
     * */
    @ResponseBody
    @PostMapping("/createfeedback")
    Response createFeedback(@RequestBody Feedback feedback) {

        LogGenerator.genLog(
                httpServletRequest,
                LogConstants.ActionName.CREATE_FEEDBACK,
                feedback.getUserId(),
                feedback
        );
        return feedbackService.createFeedback(feedback);
    }

    /**
     * <h2>用户获取评论信息</h2>
     * @param userId 用户 id
     * @return {@link Response}
     * */
    @ResponseBody
    @GetMapping("/getfeedback")
    Response getFeedback(Long userId) {

        LogGenerator.genLog(
                httpServletRequest,
                LogConstants.ActionName.GET_FEEDBACK,
                userId,
                null
        );
        return feedbackService.getFeedback(userId);
    }

    /**
     * <h2>异常演示接口</h2>
     * @return {@link Response}
     * */
    @ResponseBody
    @GetMapping("/exception")
    Response exception() throws Exception {
        throw new Exception("Welcome To IMOOC");
    }
}
