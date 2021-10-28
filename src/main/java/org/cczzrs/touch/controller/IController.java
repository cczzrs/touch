package org.cczzrs.touch.controller;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.usthe.sureness.util.JsonWebTokenUtil;

import org.cczzrs.core.constant.ProjectConstant.ROLE;
import org.cczzrs.core.log.WebLog;
import org.cczzrs.core.redis.CacheUtil;
import org.cczzrs.core.sureness.MyJwtSubject;
import org.cczzrs.core.utils.MyUtil;
import org.cczzrs.touch.controller.result.IResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiOperation;

/**
 * Controller 父类
 *
 * @RestController
 * @Api(value = "测试swagger", description = "测试swagger api") public class
 *            TestSwaggerController {
 *
 * @ApiOperation(value = "返回url中的参数", notes = "返回url中的参数")
 * @ApiImplicitParam(name = "id", value = "id值", paramType = "path", required =
 *                        true, dataType = "Integer")
 * @GetMapping(path = "/getUrlParam/{id}") public Integer
 *                  getUrlParam(@PathVariable(value = "id") Integer id) { return
 *                  id; }
 *
 */

// @Api(value = "测试swagger", description = "测试swagger api")
@RestController
@RequestMapping("/i")
public abstract class IController {

    @Resource
    CacheUtil cacheUtil;

    /**
     * 子类重写该方法
     * @return IService
     * @Autowired IUsersService iUsersService;
     * @Override public IService getIService() { return iUsersService; }
     */
    // public abstract MyService<T> getMyService();

    /**
     * 获取 HttpServletRequest 对象
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
    /**
     * 获取 HttpServletResponse 对象
     * @return HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }
    public static String getRequestIP() {
        return MyUtil.getIpAddress(getRequest());
    }
    public static String getJwt() {
        return MyJwtSubject.getJwt(getRequest());
    }
    public static Claims getJwtInfo() {
        return JsonWebTokenUtil.parseJwt(getJwt());
    }
    public static JSONObject getJwtInfoJson() {
        try {
            JSONObject jo = new JSONObject();
            jo.putAll(getJwtInfo());
            return jo;
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * @fileName MyController.java
     * @author CCZZRS
     * @date 2020-11-16 11:42:13
     * @description 获取当前登录用户
     */
    public static String getUserID() {
        // Claims claims = JsonWebTokenUtil.parseJwt(getJwt());
        try {
            return getJwtInfo().getSubject();
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * @description 获取当前登录用户的角色
     */
    public static String getAud() {
        try {
            // Claims claims = JsonWebTokenUtil.parseJwt(getJwt());
            return getJwtInfo().getAudience();
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * @fileName MyController.java
     * @author CCZZRS
     * @date 2021-08-31 14:22:29
     * @description 删除用户的数据和角色数据缓存 
     * */
    public boolean cleanAllByUser() {
        return cacheUtil.cleanAllByUser();
    }
    public boolean cleanAllByUser(String userID) {
        return cacheUtil.cleanAllByUser(userID);
    }

    /**
     * @fileName MyController.java
     * @author CCZZRS
     * @date 2020-12-31 09:00:28
     * @description 判断当前用户是否是 urms 中的任一角色
     */
    public static boolean iAm(ROLE... urms) {
        String roleID = getAud();
        roleID = roleID.substring(0, roleID.indexOf(":"));
        for (int i = 0; i < urms.length; i++) {
            if(urms[i].getID().equals(roleID))
                return true;
        }
        return false;
    }
    public static boolean iAmAnyOne() {
        return iAm(ROLE.values());
    }

    /**
     * 根据执行结果判断返回的对象类型（执行成功或失败）
     * 
     * @return IResult<String>
     */
    public static IResult<String> getResult(Integer count) {
        if (count > 0) {
            return IResult.Generator.genSuccessResult(IResult.Generator.DEFAULT_SUCCESS_MESSAGE, String.valueOf(count));
        } else if (count < -1) {
            return IResult.Generator.genResult(IResult.Code.INTERNAL_SERVER_ERROR, IResult.Code.INTERNAL_SERVER_ERROR.name(), String.valueOf(count));
        } else {
            return IResult.Generator.genResult(IResult.Code.FAIL, IResult.Code.FAIL.name(), String.valueOf(count));
        }
    }
    public static <T> IResult<T> genAccessDenied() {
        return IResult.Generator.genAccessDenied();
    }
    public static <T> IResult<T> genAccessDenied(String msg) {
        return IResult.Generator.genAccessDenied(msg);
    }
    public static <T> IResult<T> genSuccessResult() {
        return IResult.Generator.genSuccessResult();
    }
    public static <T> IResult<T> genSuccessResult(String msg) {
        return IResult.Generator.genSuccessResult(msg);
    }

    /**
     * @fileName MyController.java
     * @author CCZZRS
     * @date 2020-10-29 09:10:15
     * @description 准备做一个 统一参数验证 操作
     */
    public IResult<String> verify(String tt) {
        int delete = 0;
        return IResult.Generator.genSuccessResult(delete + "");
    }

    // public IResult<String> verifyUUID(String UUID) {
    //     if(StringUtil.isEmpty(UUID) || UUID.length() !=32){
    //         return IResult.Generator.genResult(IResult.Code.FAIL,"参数无效");
    //     }
    //     return IResult.Generator.genResult(IResult.Code.FAIL,"参数无效");
    // }

    /**
     * 批量查询
     * 
     * @return PageInfo<T>
     */
    @GetMapping("")
    @WebLog(description = "【批量查询】")
    @ApiOperation(value = "【批量查询】", notes = "get")
    public IResult<?> get(String t, @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return IResult.Generator.genSuccessResult("");
    }

    /**
     * 单个查询
     * 
     * @param id
     * @return T
     */
    @GetMapping("/{id}")
    @WebLog(description = "【单个查询】")
    @ApiOperation(value = "【单个查询】", notes = "get")
    public IResult<?> getBy(@PathVariable("id") String id) {
        return IResult.Generator.genSuccessResult("ID:"+id);
    }

    /**
     * 新增
     * 
     * @return int
     */
    @Transactional
    @PostMapping("")
    @WebLog(description = "【新增】")
    @ApiOperation(value = "【新增】", notes = "post")
    public IResult<String> post(String t) {
        return genSuccessResult();
    }

    /**
     * 更新
     * 
     * @return int
     */
    @Transactional
    @PutMapping("")
    @WebLog(description = "【更新】")
    @ApiOperation(value = "【更新】", notes = "put")
    public IResult<String> postBy(String t) {
        return genSuccessResult();
    }

    /**
     * 删除
     * 
     * @param id
     * @return int
     */
    @DeleteMapping("")
    @WebLog(description = "【删除】")
    @ApiOperation(value = "【删除】", notes = "delete")
    public IResult<String> deleteBy(String id) {
        return IResult.Generator.genResult(IResult.Code.SUCCESS,"","ID:"+id);
    }
}
