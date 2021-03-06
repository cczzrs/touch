package org.cczzrs.touch.controller.result;

import com.alibaba.fastjson.JSON;

/**
 * 统一API响应结果封装
 */
public class IResult<T> {
    private int code;
    private String message;
    private T data;

    public IResult<T> setCode(Code resultCode) {
        this.code = resultCode.code();
        return this;
    }

    public IResult<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public IResult<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public IResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
    /**
     * @fileName Code.java
     * @author CCZZRS
     * @date 2021-01-14 10:04:05
     * @description 响应码枚举，参考HTTP状态码的语义 <br/>
     * Http请求Response Code含义
            http状态返回代码 1xx（临时响应） - 表示临时响应并需要请求者继续执行操作的状态代码。
            100 （继续） 请求者应当继续提出请求。 服务器返回此代码表示已收到请求的第一部分，正在等待其余部分。
            101 （切换协议） 请求者已要求服务器切换协议，服务器已确认并准备切换。
    
            http状态返回代码 2xx （成功） - 表示成功处理了请求的状态代码。
            200 （成功） 服务器已成功处理了请求。 通常，这表示服务器提供了请求的网页。
            201 （已创建） 请求成功并且服务器创建了新的资源。
            202 （已接受） 服务器已接受请求，但尚未处理。
            203 （非授权信息） 服务器已成功处理了请求，但返回的信息可能来自另一来源。
            204 （无内容） 服务器成功处理了请求，但没有返回任何内容。
            205 （重置内容） 服务器成功处理了请求，但没有返回任何内容。
            206 （部分内容） 服务器成功处理了部分 GET 请求。
            
            http状态返回代码 3xx （重定向）- 表示要完成请求，需要进一步操作。 通常，这些状态代码用来重定向。
            300 （多种选择） 针对请求，服务器可执行多种操作。 服务器可根据请求者 (user agent) 选择一项操作，或提供操作列表供请求者选择。
            301 （永久移动） 请求的网页已永久移动到新位置。 服务器返回此响应（对 GET 或 HEAD 请求的响应）时，会自动将请求者转到新位置。
            302 （临时移动） 服务器目前从不同位置的网页响应请求，但请求者应继续使用原有位置来进行以后的请求。
            303 （查看其他位置） 请求者应当对不同的位置使用单独的 GET 请求来检索响应时，服务器返回此代码。
            304 （未修改） 自从上次请求后，请求的网页未修改过。 服务器返回此响应时，不会返回网页内容。
            305 （使用代理） 请求者只能使用代理访问请求的网页。 如果服务器返回此响应，还表示请求者应使用代理。
            307 （临时重定向） 服务器目前从不同位置的网页响应请求，但请求者应继续使用原有位置来进行以后的请求。
            
            http状态返回代码 4xx（请求错误）- 这些状态代码表示请求可能出错，妨碍了服务器的处理。
            400 （错误请求） 服务器不理解请求的语法。
            401 （未授权） 请求要求身份验证。 对于需要登录的网页，服务器可能返回此响应。
            403 （禁止） 服务器拒绝请求。
            404 （未找到） 服务器找不到请求的网页。
            405 （方法禁用） 禁用请求中指定的方法。
            406 （不接受） 无法使用请求的内容特性响应请求的网页。
            407 （需要代理授权） 此状态代码与 401（未授权）类似，但指定请求者应当授权使用代理。
            408 （请求超时） 服务器等候请求时发生超时。
            409 （冲突） 服务器在完成请求时发生冲突。 服务器必须在响应中包含有关冲突的信息。
            410 （已删除） 如果请求的资源已永久删除，服务器就会返回此响应。
            411 （需要有效长度） 服务器不接受不含有效内容长度标头字段的请求。
            412 （未满足前提条件） 服务器未满足请求者在请求中设置的其中一个前提条件。
            413 （请求实体过大） 服务器无法处理请求，因为请求实体过大，超出服务器的处理能力。
            414 （请求的 URI 过长） 请求的 URI（通常为网址）过长，服务器无法处理。
            415 （不支持的媒体类型） 请求的格式不受请求页面的支持。
            416 （请求范围不符合要求） 如果页面无法提供请求的范围，则服务器会返回此状态代码。
            417 （未满足期望值） 服务器未满足"期望"请求标头字段的要求。
            
            http状态返回代码 5xx（服务器错误）- 这些状态代码表示服务器在尝试处理请求时发生内部错误。 这些错误可能是服务器本身的错误，而不是请求出错。
            500 （服务器内部错误） 服务器遇到错误，无法完成请求。
            501 （尚未实施） 服务器不具备完成请求的功能。 例如，服务器无法识别请求方法时可能会返回此代码。
            502 （错误网关） 服务器作为网关或代理，从上游服务器收到无效响应。
            503 （服务不可用） 服务器目前无法使用（由于超载或停机维护）。 通常，这只是暂时状态。
            504 （网关超时） 服务器作为网关或代理，但是没有及时从上游服务器收到请求。
            505 （HTTP 版本不受支持） 服务器不支持请求中所用的 HTTP 协议版本。
     * 
     */
    public enum Code {
        CONTINUE(100),//（继续）
        SUCCESS(200),//成功
        REDIRECTION(300),// 重定向
        FAIL(400),//失败
        UNAUTHORIZED(401),//未认证（签名错误）
        ACCESSDENIED(403),// Forbidden服务器已理解请求，但拒绝执行它  （没权限）
        NOT_FOUND(404),//接口不存在
        INTERNAL_SERVER_ERROR(500);//服务器内部错误
    
        private final int code;
    
        Code(int code) {
            this.code = code;
        }
    
        public int code() {
            return code;
        }
    }
    
    /**
     * 响应结果生成工具
     * 请遵守开闭原则
     */
    public static class Generator {
        public static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";

        public static <T> IResult<T> genResult(Code code) {
            return genResult(code, null);
        }
        public static <T> IResult<T> genResult(Code code,String message) {
            return genResult(code, message, null);
        }
        public static <T> IResult<T> genResult(Code code,String message,T data) {
            if(data==null){
                return new IResult<T>().setCode(code).setMessage(message);
            }
            return new IResult<T>().setCode(code).setMessage(message).setData(data);
        }

        public static <T> IResult<T> genSuccessResult() {
            return genSuccessResult(DEFAULT_SUCCESS_MESSAGE);
        }
        public static <T> IResult<T> genSuccessResult(String message) {
            return genSuccessResult(message, null);
        }
        public static <T> IResult<T> genSuccessResult(String message,T data) {
            return genResult(Code.SUCCESS, message, data);
        }

        public static <T> IResult<T> genFailResult(String message) {
            return genResult(Code.FAIL, message);
        }

        public static <T> IResult<T> genAuthTokenErrResult(String message) {
            return genResult(Code.UNAUTHORIZED, message);
        }
        
        public static <T> IResult<T> genAccessDenied() {
            return genAccessDenied("您可能没有该操作权限");
        }
        public static <T> IResult<T> genAccessDenied(String message) {
            return genAccessDenied(message, null);
        }
        public static <T> IResult<T> genAccessDenied(String message, T data) {
            return genResult(Code.ACCESSDENIED, message, data);
        }
    }
}
