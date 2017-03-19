package com.gaocy.sample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by godwin on 2017/3/19.
 */

@Controller
@RequestMapping("stat")
public class StatController {

    @RequestMapping("home")
    public String loadPage() {
        return "index";
    }

}