package org.cczzrs.touch.controller;

import java.util.LinkedHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;

import org.cczzrs.core.redis.CacheUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "输入", description = "输入 api")
@RestController
@RequestMapping("/in")
public class InController {

    @Resource
    CacheUtil cacheUtil;
    
    public JSONObject newJO(){
        return new JSONObject(new LinkedHashMap<>());
    }
    public JSONObject newJO(String key, Object value){
        return new JSONObject(new LinkedHashMap<>()).fluentPut(key, value);
    }
    /**
     * @param urlData
     * @return T
     */
    @GetMapping("/{p}/{d}/**")
    @ApiOperation(value = "urlData", notes = "urlData")
    public String urlData(@PathVariable("p") String p, @PathVariable("d") String d, HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.substring(uri.indexOf("/", 1)+1);
    }
    /**
     * @param PutUrlData
     * @return T
     */
    @PutMapping("/{p}/{d}")
    @ApiOperation(value = "PutUrlData", notes = "PutUrlData")
    public String PutUrlData(@PathVariable("p") String p, @PathVariable("d") String d, JSONObject da) {

        
        return p;
    }

}