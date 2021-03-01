package com.lin.controller;

import com.lin.bean.AdminUser;
import com.lin.pojo.Users;
import com.lin.service.UsersService;
import com.lin.utils.MyJSONResult;
import com.lin.utils.PagedResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UsersService usersService;

    @GetMapping("/showList")
    public String showList(){
        return "users/usersList";
    }

    @PostMapping("/list")
    @ResponseBody
    public PagedResult list(Users users,Integer page){
        PagedResult result=usersService.queryUsers(users,page==null?1:page,10);
        return result;
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/login")
    @ResponseBody
    public MyJSONResult userLogin(String username, String password, HttpServletRequest request, HttpServletResponse response){
        //TODO 模拟登陆
        if (StringUtils.isBlank(username)||StringUtils.isBlank(password)){
            return MyJSONResult.errorMap("用户名或密码不正确");
        }else if (username.equals("luo")&&password.equals("lin")){
            String token= UUID.randomUUID().toString();
            AdminUser user=new AdminUser(username,password,token);
            request.getSession().setAttribute("sessionUser",user);
            return MyJSONResult.ok();
        }
        return MyJSONResult.errorMap("登陆失败，请重试...");
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,HttpServletResponse response){
        request.getSession().removeAttribute("sessionUser");
        return "login";

    }

}
