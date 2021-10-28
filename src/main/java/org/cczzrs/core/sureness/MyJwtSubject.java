package org.cczzrs.core.sureness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.usthe.sureness.processor.exception.ExpiredCredentialsException;
import com.usthe.sureness.processor.exception.IncorrectCredentialsException;
import com.usthe.sureness.processor.exception.SurenessAuthenticationException;
import com.usthe.sureness.processor.exception.SurenessAuthorizationException;
import com.usthe.sureness.processor.exception.UnauthorizedException;
import com.usthe.sureness.processor.exception.UnsupportedSubjectException;
import com.usthe.sureness.processor.support.JwtProcessor;
import com.usthe.sureness.subject.PrincipalMap;
import com.usthe.sureness.subject.Subject;
import com.usthe.sureness.subject.SubjectCreate;
import com.usthe.sureness.subject.creater.JwtSubjectServletCreator;
import com.usthe.sureness.subject.support.SinglePrincipalMap;
import com.usthe.sureness.util.JsonWebTokenUtil;
import com.usthe.sureness.util.SurenessConstant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

/**
 * @fileName MyJwtSubject.java
 * @author CCZZRS
 * @date 2021-08-21 10:21:14
 * @description 定制 JWT 认证对象
 *  */
@Log4j2
@Data
@Accessors(chain = true)
public class MyJwtSubject implements Subject {

    private static final long serialVersionUID = 1L;

    /**
     * 已登录角色（权限）
     */
    public static final String LOGIN_USER = "LOGIN_USER";

    public static final String PROJECTID = "TOUCH.ME";

    /** remote principalMap **/
    private PrincipalMap principalMap;

    /** the Roles which can access this resource above-targetUri **/
    private List<String> supportRoles;

    /** user identifier **/
    private String appId;
    
    /** token : admin--issueTime--refreshPeriodTime--uuid **/
    private String token;

    /** remote ip **/
    private String remoteHost;

    /** remote device **/
    private String userAgent;

    /** the roles which this user owned **/
    private List<String> ownRoles;

    /** the uri resource which this user want access **/
    private String targetUri;

    /**
     * @fileName JWTUtil.java
     * @author CCZZRS
     * @date 2021-05-13 09:55:38
     * @description 构建 token 中所需包含的用户信息
     *  */
    public static String buildJwtToken(String userID, String roleID, String ip, Map<String, Object> customClaimMap) {
        return JsonWebTokenUtil.issueJwtAll(UUID.randomUUID().toString(), 
            userID, 
            PROJECTID,
            SurenessConfiguration.TOKEN_EXPIRE_TIME,
            roleID+":"+ip,
            null,
            111111L, 
            null, 
            customClaimMap);
    }
    /**
     * @fileName MyJwtSubject.java
     * @author CCZZRS
     * @date 2021-08-21 11:51:12
     * @description 获取请求中的 jwt 值 
     * */
    public static String getJwt(Object context) {
        // ("Authorization", "Bearer eyJhbGciOiJIUzUxMi...")  --- jwt auth
        if (context instanceof HttpServletRequest) {
            String authorization = ((HttpServletRequest)context).getHeader(SurenessConstant.AUTHORIZATION);
            if (authorization==null || "".equals(authorization)) {
                return authorization.trim();
            }
        }
        return null;
    }
    public MyJwtSubject (){
    }
    public MyJwtSubject (Subject subject){
        MyJwtSubject mjs = (MyJwtSubject) subject;
        this.appId = String.valueOf(mjs.getPrincipal());
        this.token = String.valueOf(mjs.getCredential());
        this.ownRoles = (List<String>) mjs.getOwnRoles();
        this.targetUri = String.valueOf(mjs.getTargetResource());
        this.remoteHost = String.valueOf(mjs.getRemoteHost());
        this.userAgent = String.valueOf(mjs.getUserAgent());
        this.supportRoles = (List<String>) mjs.getSupportRoles();
        this.principalMap = mjs.getPrincipalMap();
    }
    /**
     * @fileName MyJwtSubject.java
     * @author CCZZRS
     * @date 2021-08-21 10:22:30
     * @description 该对象的判断是否可执行和执行创建构建工作，给后续权限认证准备数据
     *  */
    public static SubjectCreate creator(){
        return new JwtSubjectServletCreator(){
            /**
             * @fileName MyJwtSubject.java
             * @author CCZZRS
             * @date 2021-08-21 11:32:39
             * @description 验证 token 格式是否正确
             * */
            @Override
            public boolean canSupportSubject(Object context) {
                // ("Authorization", "Bearer eyJhbGciOiJIUzUxMi...")  --- jwt auth
                return !JsonWebTokenUtil.isNotJsonWebToken(MyJwtSubject.getJwt(context));
            }
            /**
             * @fileName MyJwtSubject.java
             * @author CCZZRS
             * @date 2021-08-21 11:32:39
             * @description 验证 token 格式是否正确
             * */
            @Override
            public Subject createSubject(Object context) {
                // String remoteHost = ((HttpServletRequest) context).getRemoteHost();
                // String requestUri = ((HttpServletRequest) context).getRequestURI();
                // String requestType = ((HttpServletRequest) context).getMethod();
                // String targetUri = requestUri.concat("===").concat(requestType.toLowerCase());
                // HttpServletRequest hsr = (HttpServletRequest) context;
                return new MyJwtSubject().setToken(MyJwtSubject.getJwt(context))
                    .setUserAgent(((HttpServletRequest) context).getHeader("user-agent"))
                    .setRemoteHost(((HttpServletRequest) context).getRemoteHost())
                    .setTargetResource(((HttpServletRequest) context).getRequestURI().concat("===").concat(((HttpServletRequest) context).getMethod().toLowerCase()));
            }
        };
    }

    /**
     * @fileName MyJwtProcessor.java
     * @author CCZZRS
     * @date 2021-08-21 09:34:54
     * @description 定制 JWT 处理逻辑
     *  自定义 JWT [Token] 认证的处理器 MyJwtProcessor， 继承自 JwtProcessor
     *  */
    public static JwtProcessor processor(Object usersService){
        return new JwtProcessor () {
            @Override
            public boolean canSupportSubjectClass(Class<?> var) {
                return var == MyJwtSubject.class;
            }
            @Override
            public Class<?> getSupportSubjectClass() {
                return MyJwtSubject.class;
            }
            /**
             * @fileName MyJwtProcessor.java
             * @author CCZZRS
             * @date 2021-08-21 11:18:55
             * @description 身份验证将调用以完成身份验证的接口
             *  */
            @Override
            public Subject authenticated(Subject var) throws SurenessAuthenticationException {
                try {
                    Claims claims = JsonWebTokenUtil.parseJwt(String.valueOf(var.getCredential()));
                    if (claims.getSubject()==null || "".equals(claims.getSubject())) {
                        log.error("《《《没有认证源数据》》》");
                        throw new ExpiredCredentialsException("没有认证源数据");
                    }
                    // UsersEntity user = usersService.findBy(claims.getSubject());
                    // if (user == null || StringUtil.isEmpty(user.getId())) {
                    //     log.error("《《《没有找到认证账号》》》");
                    //     throw new ExpiredCredentialsException("没有找到认证账号");
                    // } else if (user.getState()!=1) { // 当前状态(-1.注销；0.禁用；1.启用)
                    //     log.error("《《《该账号被禁用》》》");
                    //     throw new ExpiredCredentialsException("该账号被禁用");
                    // } // ...
                    PrincipalMap principalMap = new SinglePrincipalMap();
                    claims.entrySet().forEach(cmap -> principalMap.setPrincipal(cmap.getKey(), cmap.getValue()));
                    List<String> roles = new ArrayList<>(); // usersService.findOwnRoles(user.getId());
                    roles.add(LOGIN_USER);// 认证完成后给当前用户加入登录用户（ LOGIN_USER ）角色
                    log.info("=== new MyJwtSubject({})", claims.getSubject());
                    return new MyJwtSubject(var)
                        .setPrincipal(claims.getSubject())
                        .setOwnRoles(roles)
                        .setPrincipalMap(principalMap);
                } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
                    // JWT error
                    log.error("《《《JWT error》》》");
                    throw new IncorrectCredentialsException("this jwt error:" + e.getMessage());
                } catch (ExpiredJwtException e) {
                    // JWT expired
                    log.error("《《《JWT expired》》》");
                    throw new ExpiredCredentialsException("this jwt has expired");
                } catch (Exception e) {
                    // JWT expired
                    log.error("《《《JWT error expired》》》");
                    throw new IncorrectCredentialsException("this jwt error expired:" + e.getMessage());
                }
            }
            /**
             * @fileName MyJwtProcessor.java
             * @author CCZZRS
             * @date 2021-08-21 11:17:52
             * @description 授权将调用的接口，在此完成授权
             *  */
            @Override
            public void authorized(Subject var) throws SurenessAuthorizationException {
                MyJwtSubject mjs = (MyJwtSubject) var;
                List<String> roles = mjs.getOwnRoles();
                List<String> supportRoles = mjs.getSupportRoles();
                // if null, note that not config this resource
                if (supportRoles == null) {
                    log.info(" - === supportRoles == null");
                    // return;
                    // 没有配置的uri，默认不给通过
                    throw new UnsupportedSubjectException("not find uri resource");
                }
                // if config, ownRole must contain the supportRole item
                if (roles != null && supportRoles.stream().anyMatch(roles::contains)) {
                    log.info(" - === supportRoles contains roles SUCCESS");
                    return;
                }
                log.info(" - === UnauthorizedException  no permission");
                throw new UnauthorizedException("do not have the role auth to access resource");
            }
        };
    }
    @Override
    public Object getCredential() {
        return token;
    }
    public MyJwtSubject setCredential(String token) {
        this.token = token;
        return this;
    }

    @Override
    public Object getTargetResource() {
        return targetUri;
    }
    public MyJwtSubject setTargetResource(String targetUri) {
        this.targetUri = targetUri;
        return this;
    }

    @Override
    public Object getPrincipal() {
        return appId;
    }
    public MyJwtSubject setPrincipal(String appId) {
        this.appId = appId;
        return this;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void setSupportRoles(Object supportRoles) {
        this.supportRoles = (List<String>) supportRoles;
    }
    
}
