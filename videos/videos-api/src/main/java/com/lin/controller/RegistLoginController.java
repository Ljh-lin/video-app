package com.lin.controller;

import com.lin.pojo.Users;
import com.lin.pojo.VO.UsersVO;
import com.lin.service.UserService;
import com.lin.utils.IMoocJSONResult;
import com.lin.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Api(value = "用户注册登录的接口",tags = {"注册和登录的controller"})
public class RegistLoginController extends BasicController{

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户注册",notes = "用户注册的接口")
    @PostMapping("/regist")
    private IMoocJSONResult regist(@RequestBody Users user) throws Exception {

        //1.判断用户名和密码是否为空
        if (StringUtils.isBlank(user.getUsername())||StringUtils.isBlank(user.getPassword())){
            return IMoocJSONResult.errorMsg("用户名和密码不能为空");
        }
        //2.判断用户名是否存在
        boolean usernameIsExist=userService.queryUsernameIsExist(user.getUsername());

        //3.保存用户，注册信息
        if (!usernameIsExist){
            user.setNickname(user.getUsername());
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            user.setFansCounts(0);
            user.setReceiveLikeCounts(0);
            user.setFollowCounts(0);
            userService.saveUser(user);
        }else {
            return IMoocJSONResult.errorMsg("用户名已经存在，请换一个再试");
        }

        user.setPassword("");

        UsersVO usersVo=setUserRedisSessionToken(user);

        return IMoocJSONResult.ok(usersVo);
    }

    @ApiOperation(value = "用户登录",notes = "用户登录的接口")
    @PostMapping("/login")
    public IMoocJSONResult login(@RequestBody Users user) throws Exception {
        String username=user.getUsername();
        String password=user.getPassword();

        //1.判断用户名和密码必须不为空
        if (StringUtils.isBlank(username)||StringUtils.isBlank(password)){
            return IMoocJSONResult.ok("用户名和密码不能为空");
        }

        //2.判断用户是否存在
        Users userResult=userService.queryUserForLogin(username,
                MD5Utils.getMD5Str(user.getPassword()));

        //3.返回
        if (userResult!=null){
            userResult.setPassword("");
            UsersVO usersVo=setUserRedisSessionToken(userResult);
            return IMoocJSONResult.ok(usersVo);
        }else {
            return IMoocJSONResult.errorMsg("用户名或密码不正确,请重试...");
        }

    }

    @ApiOperation(value = "用户注销",notes = "用户注销的接口")
    @ApiImplicitParam(name = "userId",value = "用户Id",required = true,
                        dataType = "String",paramType = "query")
    @PostMapping("/logout")
    public IMoocJSONResult logout(String userId) throws Exception {
        redis.del(USER_REDIS_SESSION+":"+userId);
       return IMoocJSONResult.ok();

    }




    /**
     * 生成无状态的redis-session
     * @param userModel
     * @return
     */
    public UsersVO setUserRedisSessionToken(Users userModel){
        String uniqueToken= UUID.randomUUID().toString();
        redis.set(USER_REDIS_SESSION+":"+userModel.getId(),uniqueToken,1000*60*30);
        UsersVO usersVo=new UsersVO();
        BeanUtils.copyProperties(userModel,usersVo);
        usersVo.setUserToken(uniqueToken);
        return usersVo;
    }
}
