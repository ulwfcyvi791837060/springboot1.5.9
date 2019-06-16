package com.yyx.aio.controller;

import com.yyx.aio.entity.Result;
import com.yyx.aio.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @Autowired
    UserService userServiceimpl;

    @GetMapping(value = "/index")
    public String index2() {
        return "index";
    }

    @GetMapping(value = "/")
    public String index() {
        return "index";
    }

    @GetMapping(value = "/upload")
    public String upload() {
        return "upload";
    }


    @PostMapping(value = "/upload_action")
    public String uploadAction(Model model,@RequestParam("date") String date, RedirectAttributes redirectAttributes) {
        Result result = userServiceimpl.uploadAction(date, false);
        model.addAttribute("name", result.getMsg());
        redirectAttributes.addFlashAttribute("name", result.getMsg());
        return "redirect:/upload";
    }
}
