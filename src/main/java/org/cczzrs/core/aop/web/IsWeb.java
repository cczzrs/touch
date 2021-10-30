package org.cczzrs.core.aop.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.ApiOperation;

/**
 * @fileName IsWeb.java
 * @author CCZZRS
 * @date 2020-10-31 10:42:20
 * @description web接口基础周边
 */
public @interface IsWeb {
    @RequestMapping(value={Web.VALUE_DEL}, method=RequestMethod.GET)
    @ApiOperation(value = Web.DESCRIPTION_DEL, notes = Web.VALUE_DEL)
    public @interface Web {
        public static final RequestMethod METHOD_DEL = RequestMethod.GET;
        public static final String VALUE_DEL = "GET";
        public static final String DESCRIPTION_DEL = "GET";

        String value() default VALUE_DEL;
        /**
         * 日志描述信息
         * @return
         */
        String description() default DESCRIPTION_DEL;
        
        /**
         * 是否打印返回结果数据
         * @return
         */
        boolean isPrintResult() default false;
    }
    // @GetMapping("")
    @ApiOperation(value = Web.VALUE_DEL, notes = Web.VALUE_DEL)
    public @interface Get {
        public static final RequestMethod METHOD_DEL = RequestMethod.GET;
        public static final String VALUE_DEL = "GET";
        String description() default VALUE_DEL;
        boolean isPrintResult() default false;
    }
}
