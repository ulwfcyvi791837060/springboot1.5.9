package com.yyx.aio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * <p>
 * 首页Controller
 * </p>
 *
 * @package: com.xkcoding.upload.controller
 * @description: 首页Controller
 * @author: shenyangkai
 * @date: Created in 2018/10/20 21:22
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: shenyangkai
 */
@Controller
public class IndexController {
    @GetMapping(value = "/index")
    public String index2() {
        return "index";
    }

    @GetMapping(value = "/")
    public String index() {
        return "index";
    }
}
