package com.yyx.aio.controller;

/**
 * @author Xhero
 * @create 2017-11-28-9:46
 */

import com.yyx.aio.common.entity.ResponseEntity;
import com.yyx.aio.common.entity.SuccessResponseEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {
    @PostMapping("/users")
    public ResponseEntity listUsers(){

        return new SuccessResponseEntity();
    }
    @ApiOperation(value="测试", notes="")
    @GetMapping("/test")
    public ResponseEntity test(){
        return  new SuccessResponseEntity("Hello World");
    }
}
