package com.codebroker.web.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")

public class IndexController {

    @RequestMapping(value="/",method = {RequestMethod.GET})
    @ApiOperation(value ="index",hidden = true)
    public String index(){
        return "redirect:/swagger-ui.html";
    }

}
