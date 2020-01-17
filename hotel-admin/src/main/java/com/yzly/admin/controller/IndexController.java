package com.yzly.admin.controller;

import com.yzly.admin.annotation.PermissionLimit;
import com.yzly.admin.domain.ReturnT;
import com.yzly.admin.service.UsersApiService;
import com.yzly.core.domain.admin.Users;
import com.yzly.core.service.admin.UsersService;
import lombok.extern.apachecommons.CommonsLog;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lazyb
 * @create 2019/11/25
 * @desc
 **/
@Controller
@CommonsLog
public class IndexController {

    @Autowired
    private UsersApiService usersApiService;
    @Autowired
    private UsersService usersService;

    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView("/index");
        view.addObject("now", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        return view;
    }

    @RequestMapping(value = "/toLogin")
    @PermissionLimit(limit = false)
    public String loginForm(HttpServletRequest request, HttpServletResponse response) {
        if (usersApiService.ifLogin(request, response) != null) {
            return "redirect:/";
        }
        return "login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    @PermissionLimit(limit = false)
    public String registerForm() {
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @PermissionLimit(limit = false)
    public String register(@RequestParam("name") String name, @RequestParam("password") String password) {
        log.info("register: name:" + name + ",password:" + password);
        Users admin = usersService.register(name, password);
        if (admin == null) {
            return "redirect:/register";
        } else {
            return "redirect:/login";
        }
    }

    @RequestMapping(value = "login", method=RequestMethod.POST)
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> login(HttpServletRequest request, HttpServletResponse response, String userName, String password, String ifRemember) {
        boolean ifRem = (ifRemember!=null && ifRemember.trim().length()>0 && "on".equals(ifRemember))?true:false;
        return usersApiService.login(request, response, userName, password, ifRem);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        usersApiService.logout(request, response);
        redirectAttributes.addFlashAttribute("message", "您已安全退出");
        return "redirect:/login";
    }

    @RequestMapping("/403")
    public String unauthorizedRole() {
        return "403";
    }

    @RequestMapping("/index/hotel")
    public String hotels() {
        return "hotel";
    }

    @RequestMapping("/index/syn_hotel_info")
    public String synHotels() {
        return "syn_hotel_info";
    }

    @RequestMapping("/index/order_info")
    public String orderInfo() {
        return "order_info";
    }

    @RequestMapping("/index/room_price")
    public String roomPrice() {
        return "room_price";
    }

}
