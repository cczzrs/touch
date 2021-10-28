package org.cczzrs.core.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @fileName ProjectConstant.java
 * @author CCZZRS
 * @date 2021-04-26 10:36:42
 * @description 项目配置信息 - 初始化 
 **/
@Order(-1)
@Component
@ConfigurationProperties("projectp-config")
public class ProjectConstant {

    public static String APPID;
    public void setAppid(String appid) {
        ProjectConstant.APPID = appid;
    }

    // public static Properties applicationProperties = PropertyUtil.readProperties("application.properties");

    public static String ENV = "prod";
    @Value("${spring.profiles.active}")
    public void setEnv(String env) {
        ProjectConstant.ENV = env;
    }
    public static boolean isProd() {
        return ProjectConstant.ENV == "prod";
    }
    /**
     * @fileName ProjectConstant.java
     * @author CCZZRS
     * @date 2020-12-31 09:45:17
     * @description 数据库所有角色，ID、Code数据一样
     *  */
    public enum ROLE {
        ROLE_ADMIN_CODE(),  // admin
        ROLE_KEFU_CODE(),   // 客服
        ROLE_SFJ_CODE(),    // 司法局管理人员
        ROLE_BM_CODE(),     // 部门管理人员
        ROLE_ZZJG_CODE(),   // 组织机构管理人员
        ROLE_TJYZ_CODE(),   // 调解援助人员
        ROLE_LBX_CODE(),    // 老百姓
        ROLE_DW_CODE(),     // 单位管理人员
        ROLE_HD_CODE(),     // 参加活动人员
        ROLE_ZF_CODE(),     // 执法人员
        ROLE_SP_CODE();     // 审批人员

        public String getID() {
            return this.name();
        }
        public String getCode() {
            return this.name();
        }
    }
}
