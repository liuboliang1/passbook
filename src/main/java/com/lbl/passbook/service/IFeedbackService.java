package com.lbl.passbook.service;

import com.lbl.passbook.vo.Feedback;
import com.lbl.passbook.vo.Response;

/**
 * 评论功能相关实现
 */
public interface IFeedbackService {
    /**
     * <h2>创建评论</h2>
     * @param feedback {@link Feedback}
     * @return {@link Response}
     * */
    Response createFeedback(Feedback feedback);

    /**
     * <h2>获取用户评论</h2>
     * @param userId 用户 id
     * @return {@link Response}
     * */
    Response getFeedback(Long userId);
}
