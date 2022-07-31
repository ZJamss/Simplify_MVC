package com.business.controller;

import com.business.pojo.User;
import com.business.service.UserService;
import com.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @Program: simplify_mvc
 * @Description:
 * @Author: ZJamss
 * @Create: 2022-07-31 13:34
 **/
@Controller
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping("/user/query")
    public String queryUser(HttpServletRequest request, HttpServletResponse response, @RequestParam("username") String username, String passwd) {
        response.setContentType("text/html;charset=utf-8");
        User user = userService.getUser(username, passwd);
        request.setAttribute("user",user);
        return "redirect:/user.jsp";
    }

    @ResponseBody
    @RequestMapping("/user/list")
    public List<User> userList(){
        return userService.userList();
    }

}
