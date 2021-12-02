package org.cczzrs.touch.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Controller html页面
 *
 */

@Api(value = "html页面", description = "html页面")
@Controller
@RequestMapping({""})
public class PageController {

    /**
     * 批量查询
     * 
     * @return PageInfo<T>
     */
    @GetMapping({"/","/home","/index","/index.htm"})
    @ApiOperation(value = "home", notes = "home")
    public String home(String t) {
        return "index.html";
    }

    /**
     * @param html name
     * @return T
     */
    @GetMapping("/p/{n}")
    @ApiOperation(value = "指定html", notes = "getHTML")
    public String getHTML(@PathVariable("n") String n) {
        return n;
    }
    /**
     * @param file name
     * @return T
     */
    @GetMapping("/p/{p}/{n}")
    @ApiOperation(value = "指定file", notes = "getFile")
    public String getFile(@PathVariable("p") String p, @PathVariable("n") String n) {
        return p+"/"+n;
    }
    /**
     * @param file name
     * @return T
     */
    @GetMapping("/p/{p}/{d}/**")
    @ApiOperation(value = "指定dfile", notes = "getDFile")
    public String getDFile(@PathVariable("p") String p, @PathVariable("d") String d, HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.substring(uri.indexOf("/", 1)+1);
    }


}
