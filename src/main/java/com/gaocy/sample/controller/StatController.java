package com.gaocy.sample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by godwin on 2017/3/19.
 */

@Controller
@RequestMapping("")
public class StatController {

    @RequestMapping("")
    public String loadPage(HttpServletRequest req, HttpServletResponse resp, Model model) {
        String remoteHost = req.getRemoteHost();
        int remotePort = req.getRemotePort();
        String addr = remoteHost + ":" + remotePort;
        System.out.println("remoteAddr: " + addr);
        model.addAttribute("ip", addr);
        return "index";
    }

}