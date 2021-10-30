package org.cczzrs.core.sureness;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.usthe.sureness.matcher.DefaultPathRoleMatcher;
import com.usthe.sureness.matcher.PathTreeProvider;
import com.usthe.sureness.matcher.TreePathRoleMatcher;
import com.usthe.sureness.mgt.SurenessSecurityManager;
import com.usthe.sureness.processor.DefaultProcessorManager;
import com.usthe.sureness.processor.Processor;
import com.usthe.sureness.processor.ProcessorManager;
import com.usthe.sureness.subject.SubjectFactory;
import com.usthe.sureness.subject.SurenessSubjectFactory;
import com.usthe.sureness.subject.creater.NoneSubjectServletCreator;
import com.usthe.sureness.util.JsonWebTokenUtil;

import org.cczzrs.core.utils.MyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * sureness config
 * @author tomsun28
 * @date 22:40 2020-03-02
 */
@Configuration
@ConfigurationProperties("sureness-config")
public class SurenessConfiguration {

    @Value("${spring.application.name}")
    String applicationName;
    
    @Value("${server.servlet.context-path}")
    String contextPath;

    public static String SECRETKEY = "!fazhi365.net!";
    
    public void setSecretKey(String secretKey){
        SECRETKEY = secretKey;
        TOM_SECRET_KEY = MyUtil.encryptSHA(SECRETKEY);
    }
    public static Long TOKEN_EXPIRE_TIME = 86400L;
    public void setTokenExpireTime(Long tokenExpireTime){
        TOKEN_EXPIRE_TIME = tokenExpireTime;
    }
    public static Long TOKEN_REDIS_EXPIRE_TIME = 86400L;
    public void setTokenRedisExpireTime(Long tokenRedisExpireTime){
        TOKEN_REDIS_EXPIRE_TIME = tokenRedisExpireTime;
    }
    /**
     * Token secret key
     */
    public static String TOM_SECRET_KEY = MyUtil.encryptSHA(SECRETKEY);
            //     "?::4s9ssf2sf4sed45pf):" +
            // "RnLN7XNn4wARoQXizIv6MHUsIV+EFfiMw/x7R0ntu4aWr/CWuApcFaj" +
            // "CyaFv0bwq2Eik0jdrKUtsA6bx3sDJeFV643R+YYzGMRIqcBIp6AKA98" +
            // "GM2RIqcBIp6-?::4390fsf4sdl6opf)4ZI:tdQMtcQQ14pkOAQdQ546";

    @Bean
    ProcessorManager processorManager() { // Object usersService) { // 处理器Processor初始化
        List<Processor> processorList = new LinkedList<>();
        // 使用了默认的支持NoneSubject的处理器NoneProcessor 
        // NoneProcessor noneProcessor = new NoneProcessor();
        // processorList.add(noneProcessor);
        // 使用了默认的支持JwtSubject的处理器JwtProcessor  
        // JwtProcessor jwtProcessor = new JwtProcessor();
        // processorList.add(jwtProcessor);
        // 使用了默认的支持PasswordSubject的处理器PasswordProcessor  
        // PasswordProcessor passwordProcessor = new PasswordProcessor();
        // 这里注意，PasswordProcessor需要对用户账户密码验证，所以其需要账户信息提供者来给他提供想要的账户数据，
        // 这里的 SurenessAccountProvider accountProvider bean就是这个账户数据提供源，
        // 其实现bean是上面讲到的 DatabaseAccountProvider bean,即数据库实现的账户数据提供者。 
        // passwordProcessor.setAccountProvider(accountProvider);
        // processorList.add(passwordProcessor);
        // 自定义token认证  use custom token processor
        // CustomTokenProcessor customTokenProcessor = new CustomTokenProcessor();
        // customTokenProcessor.setAccountProvider(accountProvider);
        // processorList.add(customTokenProcessor);
        
        // 使用自定义 JWT [Token] 认证的处理器 MyJwtSubject.processor 继承自 JwtProcessor
        processorList.add(MyJwtSubject.processor()); // usersService));
        return new DefaultProcessorManager(processorList);
    }
    @Bean("databasePathTreeProvider")
    PathTreeProvider uriAuthConfig(){ // Object authResourceService){
        return new PathTreeProvider() {
            /**
             * @fileName DatabasePathTreeProvider.java
             * @author CCZZRS
             * @date 2021-08-21 16:00:06
             * @description 需要认证的资源
             *  */
            @Override
            public Set<String> providePathData() {
                // MyExamples me = new MyExamples(AuthResourceEntity.class, AuthRoleResourceEntity.class, AuthRoleEntity.class);
                // me.as("ars","arrs","ar").addJoins("arrs.ResourceID=ars.ID", "ar.ID=arrs.RoleID");
                // me.get("ars").and().andEqualTo("status", 1);
                // me.selectColumns.add("ars.Uri AS uri");
                // me.selectColumns.add("ars.Method AS method");
                // me.selectColumns.add("CONCAT( '[', IFNULL( GROUP_CONCAT( ar.`Code` ), '' ), ']' ) AS roles");
                // me.groupByClause = "ars.ID";
                // return SurenessCommonUtil.attachContextPath(getContextPath(), authResourceService.findsBy(me).stream().map(r -> String.valueOf(r.get("uri"))+"==="+String.valueOf(r.get("method"))+"==="+String.valueOf(r.get("roles"))).collect(Collectors.toSet()));
                Set<String> set = new HashSet<>();
                // set.add("/api/v2/host===post===[role1,role5]");
                set.add("/users/*===get===[role2,role3,role4]");
                return set;
            }
            /**
             * @fileName DatabasePathTreeProvider.java
             * @author CCZZRS
             * @date 2021-08-21 16:00:06
             * @description 不需要认证的资源
             *  */
            @Override
            public Set<String> provideExcludedResource() {
                // return SurenessCommonUtil.attachContextPath(getContextPath(), authResourceService.findsBy("status", 9).stream().map(r -> r.getUri()+"==="+r.getMethod()).collect(Collectors.toSet()));
                Set<String> set = new HashSet<>();
                set.add("/**/*.html===get");
                set.add("/**/*.js===get");
                set.add("/**/*.css===get");
                set.add("/**/*.ico===get");
                set.add("/**/*.map===get");
                set.add("/**/favicon*.png===get");
                set.add("/v2/api-docs===get");
                set.add("/webjars/**===*");
                set.add("/swagger-resources/**===*");
                set.add("/druid/**===*");
                set.add("/users/login===post");
                set.add("/users===get");
                return set;
            }
        };
    }
    /**
     * @param databasePathTreeProvider the path tree resource load from database
     */
    @Bean
    TreePathRoleMatcher pathRoleMatcher(PathTreeProvider databasePathTreeProvider) {
        // 这里的PathTreeProvider databasePathTreeProvider 就是通过数据库来提供资源权限配置信息bean实例
        // 下面我们再实例化一个通过文件sureness.yml提供资源权限配置信息的提供者
        // PathTreeProvider documentPathTreeProvider = new DocumentPathTreeProvider();
        // 下面我们再实例化一个通过注解形式@RequiresRoles @WithoutAuth提供资源权限配置信息的提供者
        // AnnotationPathTreeProvider annotationPathTreeProvider = new AnnotationPathTreeProvider();
        // 设置注解扫描包路径，也就是你提供api的controller路径 
        // annotationPathTreeProvider.setScanPackages(Collections.singletonList("com.usthe.sureness.sample.tom.controller"));
        // 开始初始化资源权限匹配器，我们可以把上面三种提供者都加入到匹配器中为其提供资源权限数据，匹配器中的数据就是这三个提供者提供的数据集合。t
        DefaultPathRoleMatcher pathRoleMatcher = new DefaultPathRoleMatcher();
        // pathRoleMatcher.setPathTreeProvider(documentPathTreeProvider);
        // pathRoleMatcher.setPathTreeProvider(annotationPathTreeProvider);
        if(null != contextPath && !"".equals(contextPath)) {databasePathTreeProvider.setContextPath(contextPath);}// 如果的部署环境则添加项目部署的根路径（tomcat下war包名称）
        pathRoleMatcher.setPathTreeProvider(databasePathTreeProvider);
        // 使用资源权限配置数据来建立对应的匹配树
        pathRoleMatcher.buildTree();
        return pathRoleMatcher;
    }

    @Bean
    SubjectFactory subjectFactory() {
        // 我们之前知道了可以有各种Processor来处理对应的Jwt，那这Subject怎么得到呢，就需要不同的SubjectCreator来根据请求信息创建对应的Subject对象供之后的流程使用
        SubjectFactory subjectFactory = new SurenessSubjectFactory();
        // 这里我们注册我们需要的SubjectCreator
        subjectFactory.registerSubjectCreator(Arrays.asList(
                // 注意! 强制必须首先添加一个 noSubjectCreator
                new NoneSubjectServletCreator(),
                // 注册用来创建PasswordSubject的creator
                // new BasicSubjectServletCreator(),
                // 注册用来创建JwtSubject的creator
                // new JwtSubjectServletCreator(),
                // 当然你可以自己实现一个自定义的creator，实现SubjectCreate接口即可
                // new CustomPasswdSubjectCreator(),
                // use custom token creator
                // new CustomTokenSubjectCreator()));
                
                // Jwt 的判断是否可执行和执行创建构建工作，给后续权限认证准备数据
                 MyJwtSubject.creator()));
        return subjectFactory;
    }

    @Bean
    SurenessSecurityManager securityManager(ProcessorManager processorManager,
                                            TreePathRoleMatcher pathRoleMatcher, SubjectFactory subjectFactory) {
        JsonWebTokenUtil.setDefaultSecretKey(TOM_SECRET_KEY);
        // 我们把上面初始化好的配置bean整合到一起初始化surenessSecurityManager 
        SurenessSecurityManager securityManager = SurenessSecurityManager.getInstance();
        // 设置资源权限匹配者
        securityManager.setPathRoleMatcher(pathRoleMatcher);
        // 设置subject创建工厂
        securityManager.setSubjectFactory(subjectFactory);
        // 设置处理器processor管理者
        securityManager.setProcessorManager(processorManager);
        return securityManager;
    }


}
