package org.cczzrs.core.swagger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Profile({ "test", "dev" })// 配置只允许在开发和测试的时候暴露所有的接口
// @PropertySourcedMapping(propertyKey = "springfox.documentation.swagger.v2.path", value = "/zhzf/swagger")
public class SwaggerConfig{ // extends WebMvcConfigurerAdapter {

    public static final String AUTHORIZATION = "Authorization";

    @Bean
    public Docket docker() {
        // 添加head参数配置start
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name(AUTHORIZATION).description("Token令牌").modelRef(new ModelRef("string"))
                .parameterType("header").required(false).build();
        pars.add(tokenPar.build());
        // 构造函数传入初始化规范，这是swagger2规范
        return new Docket(DocumentationType.SWAGGER_2)
                // 配置是否启用Swagger，如果是false，在浏览器将无法访问，默认是true
                .groupName("ALL").enable(true)
                // apiInfo：添加api详情信息，参数为ApiInfo类型的参数，这个参数包含了第二部分的所有信息
                .apiInfo(apiInfo()).select()
                // apis： 添加过滤条件,
                .apis(RequestHandlerSelectors.basePackage("org.cczzrs.touch.controller"))
                // .paths(PathSelectors.any())
                // paths： 这里是控制哪些路径的api会被显示出来，比如下方的参数就是除了/user以外的其它路径都会生成api文档
                // .paths((String a) -> !"/user".equals(a))
                .build().directModelSubstitute(Timestamp.class, Date.class).globalOperationParameters(pars);
    }

    private ApiInfo apiInfo() {
        return new ApiInfo("TOUCH", // 标题
                "触碰1.0接口API", // 描述
                "版本内容：v1.0", // 版本
                "组织：https://gitee.com/cczzrs", // 组织链接
                new Contact("CCZZRS", "https://0-0.cc/", "1161717099@qq.com"), // 联系人信息
                "许可：Apach 2.0 ", // 许可
                "许可链接：https://gitee.com/cczzrs", // 许可连接
                Arrays.asList(new StringVendorExtension("CCZZRS", "13461346384"))// 扩展
        );
    }

    /*
     * Swagger常用注解
     *
     * @Api：修饰整个类，描述Controller的作用；
     * 
     * @ApiOperation：描述一个类的一个方法，或者说一个接口；
     * 
     * @ApiParam：单个参数描述；
     * 
     * @ApiModel：用对象来接收参数；
     * 
     * @ApiProperty：用对象接收参数时，描述对象的一个字段；
     * 
     * @ApiResponse：HTTP响应其中1个描述；
     * 
     * @ApiResponses：HTTP响应整体描述；
     * 
     * @ApiIgnore：使用该注解忽略这个API；
     * 
     * @ApiError ：发生错误返回的信息；
     * 
     * @ApiImplicitParam：一个请求参数；
     * 
     * @ApiImplicitParams：多个请求参数。 编写RESTful API接口 Spring Boot中包含了一些注解，对应于HTTP协议中的方法：
     * 
     * @GetMapping对应HTTP中的GET方法；
     * 
     * @PostMapping对应HTTP中的POST方法；
     * 
     * @PutMapping对应HTTP中的PUT方法；
     * 
     * @DeleteMapping对应HTTP中的DELETE方法；
     * 
     * @PatchMapping对应HTTP中的PATCH方法。
     **/

}
