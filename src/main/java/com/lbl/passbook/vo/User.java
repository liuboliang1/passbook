package com.lbl.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /** 用户id */
    private Long id;

    /** 用户基本信息 */
    private BaseInfo baseInfo;

    /** 用户其他信息 */
    private OtherInfo otherInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseInfo {
        /** 用户名 */
        private String name;
        /** 用户年龄 */
        private Integer age;
        /** 用户性别 */
        private String sex;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OtherInfo {
        /** 联系电话 */
        private String phone;
        /** 地址 */
        private String address;
    }

}
