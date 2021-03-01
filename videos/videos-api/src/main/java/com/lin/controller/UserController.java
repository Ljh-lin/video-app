package com.lin.controller;

import com.lin.pojo.Users;
import com.lin.pojo.UsersReport;
import com.lin.pojo.VO.PublisherVideo;
import com.lin.pojo.VO.UsersVO;
import com.lin.service.UserService;
import com.lin.utils.IMoocJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


@RestController
@Api(value = "用户相关业务的接口",tags = {"用户相关业务的controller"})
@RequestMapping("/user")
public class UserController extends BasicController{

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户上传头像",notes = "用户上传头像的接口")
    @ApiImplicitParam(name = "userId",value = "用户id",required = true,
                        dataType = "String",paramType = "query")
    @PostMapping("/uploadFace")
    private IMoocJSONResult uploadFace(String userId,
                                       @RequestParam("file")MultipartFile[] files) throws IOException {

        if (StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id不能为空");
        }

        //保存到数据库中的相对路径
        String uploadPathDB="/"+userId+"/face";
        FileOutputStream fileOutputStream=null;
        InputStream inputStream=null;
        try {
            if (files!=null&&files.length>0){
                String fileName=files[0].getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)){
                    //文件上传的最终路径
                    String finalFacePath=FILE_SPACE+uploadPathDB+"/"+fileName;
                    //设置数据库保存的路径
                    uploadPathDB+=("/"+fileName);

                    File outFile=new File(finalFacePath);
                    if (outFile.getParentFile()!=null||!outFile.getParentFile().isDirectory()){
                        //创建文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream=new FileOutputStream(outFile);
                    inputStream=files[0].getInputStream();
                    IOUtils.copy(inputStream,fileOutputStream);
                }
            }else {
                return IMoocJSONResult.errorMsg("上传出错...");
            }
        }catch (Exception e){
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错...");

        }finally {
            if (fileOutputStream!=null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        Users user=new Users();
        user.setId(userId);
        user.setFaceImage(uploadPathDB);
        userService.updateUserInfo(user);

        return IMoocJSONResult.ok(uploadPathDB);
    }


    @ApiOperation(value = "查询用户信息",notes = "查询用户信息的接口")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "userId",value = "用户id",required = true,
            dataType = "String",paramType = "query"),
             @ApiImplicitParam(name = "fanId",value = "粉丝id",required = true,
                            dataType = "String",paramType = "query")
            })

    @PostMapping("/query")
    public IMoocJSONResult query(String userId,String fanId){

        if (StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id不能为空...");
        }

        Users userInfo=userService.queryUserInfo(userId);
        UsersVO usersVo=new UsersVO();
        BeanUtils.copyProperties(userInfo,usersVo);

        usersVo.setFollow(userService.queryIfFollow(userId,fanId));

        return IMoocJSONResult.ok(usersVo);
    }

    @ApiOperation(value = "查询视频发布者信息",notes = "查询视频发布者信息的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginUserId",value = "登录用户Id",required = true,dataType = "String"),
            @ApiImplicitParam(name = "videoId",value = "视频Id",required = true,dataType = "String"),
            @ApiImplicitParam(name = "publishUserId",value = "发布者ID",required = true,dataType = "String"),

    })
    @PostMapping("/queryPublisher")
    public IMoocJSONResult queryPublisher(String loginUserId,String videoId,String publishUserId){
        if (StringUtils.isBlank(publishUserId)){
            return IMoocJSONResult.errorMsg("获取失败！");
        }

        //1.查询视频发布者的信息
        Users userInfo=userService.queryUserInfo(publishUserId);
        UsersVO pulisher=new UsersVO();
        BeanUtils.copyProperties(userInfo,pulisher);

        //2.查询当前登录者和视频的点赞关系
        boolean userLikeVideo=userService.isUserLikeVideo(loginUserId,videoId);

        PublisherVideo bean=new PublisherVideo();
        bean.setPublisher(pulisher);
        bean.setUserLikeVideo(userLikeVideo);

        return IMoocJSONResult.ok(bean);
    }

    @ApiOperation(value = "关注",notes = "关注某个视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",required = true, dataType = "String"),
            @ApiImplicitParam(name = "fanId",value = "粉丝Id",required = true,dataType = "String")
    })
    @PostMapping("/beyourfans")
    public IMoocJSONResult beyourfans(String userId,String fanId){

        if (StringUtils.isBlank(userId)||StringUtils.isBlank(fanId)){
            return IMoocJSONResult.errorMsg("");
        }

        userService.saveUserFanRelation(userId,fanId);
        return IMoocJSONResult.ok("关注成功...");
    }


    @ApiOperation(value = "取消关注",notes = "取消关注某个视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id",required = true, dataType = "String"),
            @ApiImplicitParam(name = "fanId",value = "粉丝Id",required = true,dataType = "String")
    })
    @PostMapping("/dontbeyourfans")
    public IMoocJSONResult dontbeyourfans(String userId,String fanId){

        if (StringUtils.isBlank(userId)||StringUtils.isBlank(fanId)){
            return IMoocJSONResult.errorMsg("");
        }

        userService.deleteUserFanRelation(userId,fanId);
        return IMoocJSONResult.ok("取消关注成功...");
    }

    @ApiOperation(value = "举报用户",notes = "举报用户的接口")
    @ApiImplicitParam(name = "userReport",value = "举报内容",required = true,paramType = "from")
    @PostMapping("/reportUser")
    public IMoocJSONResult reportUser(@RequestBody UsersReport usersReport){

       //保存举报信息

        userService.reportUser(usersReport);
        return IMoocJSONResult.ok("举报成功！！！");
    }




}
