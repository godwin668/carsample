package com.gaocy.sample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by godwin on 2017/3/19.
 */

@Controller
@RequestMapping("stat")
public class StatController {

    @RequestMapping("home")
    public String loadPage(HttpServletRequest req, HttpServletResponse resp) {
        String remoteAddr = req.getRemoteAddr();
        String remoteHost = req.getRemoteHost();
        int remotePort = req.getRemotePort();
        System.out.println("remoteAddr: " + remoteAddr);
        System.out.println("remoteHost: " + remoteHost);
        System.out.println("remotePort: " + remotePort);
        return "index";
    }

}