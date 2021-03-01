package com.lin.controller;

import com.lin.enums.VideoStatusEnum;
import com.lin.pojo.Bgm;
import com.lin.pojo.Comments;
import com.lin.pojo.Users;
import com.lin.pojo.VO.CommentVO;
import com.lin.pojo.Videos;
import com.lin.service.BgmService;
import com.lin.service.VideoService;
import com.lin.utils.FetchVideoCover;
import com.lin.utils.IMoocJSONResult;
import com.lin.utils.MergeVideoMp3;
import com.lin.utils.PagedResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@Api(value = "视频相关业务的接口", tags = {"视频相关业务的controller"})
@RequestMapping("/video")
public class VideoController extends BasicController {

    @Autowired
    private BgmService bgmService;

    @Autowired
    private VideoService videoService;


    @ApiOperation(value = "上传视频", notes = "上传视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "bgmId", value = "背景音乐id", required = false,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds", value = "背景音乐播放长度", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "desc", value = "视频描述", required = false,
                    dataType = "String", paramType = "form")
    })
    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    public IMoocJSONResult upload(String userId,
                                  String bgmId, double videoSeconds,
                                  int videoWidth, int videoHeight,
                                  String desc,
                                  @ApiParam(value = "短视频", required = true)
                                          MultipartFile file) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("用户id不能为空");
        }

        //保存到数据库中视频和封面的相对路径
        String uploadPathDB = "/" + userId + "/video";
        String coverPathDB = "/" + userId + "/video" + "/cover";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;

        //文件上传的最终路径
        String finalVideoPath = "";
        //封面截图的最终路径
        String finalCoverPath = "";

        try {
            if (file != null) {
                String fileName = file.getOriginalFilename();

                //视频文件名称
                String fileNamePrefix = fileName.split("\\.")[0];

                if (StringUtils.isNotBlank(fileName)) {
                    finalVideoPath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    finalCoverPath = FILE_SPACE + coverPathDB + "/" + fileNamePrefix + ".jpg";
                    //设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);
                    coverPathDB += ("/" + fileNamePrefix + ".jpg");

                    File outFile1 = createdir(finalVideoPath);
                    fileOutputStream = new FileOutputStream(outFile1);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);

                    File outFile2 = createdir(finalCoverPath);
                    FileOutputStream fileOutputStream2 = new FileOutputStream(outFile2);
                    IOUtils.copy(inputStream, fileOutputStream2);


//                    File outFile1 = new File(finalVideoPath);
//                    File outFile2=new File(finalCoverPath);
//                    if (outFile1.getParentFile() != null || !outFile1.getParentFile().isDirectory()) {
//                        //创建文件夹
//                        outFile1.getParentFile().mkdirs();
//                    }
                }
            } else {
                return IMoocJSONResult.errorMsg("上传出错...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错...");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        //判断bgmId是否为空，如果不为空
        //查询bgm信息，并且合并视频，生成新的视频
        if (StringUtils.isNotBlank(bgmId)) {
            Bgm bgm = bgmService.queryBgmById(bgmId);
            String mp3InputPath = FILE_SPACE + bgm.getPath();


            MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
            String videoInputPath = finalVideoPath;

            String videoOutPathName = UUID.randomUUID().toString() + "_wx" + ".mp4";
            //数据库存储路径
            uploadPathDB = "/" + userId + "/video" + "/" + videoOutPathName;
            //完整路径
            finalVideoPath = FILE_SPACE + uploadPathDB;
            tool.convertor(videoInputPath, mp3InputPath, videoSeconds, finalVideoPath);

        }
        System.out.println("uploadPathDB:" + uploadPathDB);
        System.out.println("finalVideoPath" + finalVideoPath);

        //视频截图
        FetchVideoCover videoCover = new FetchVideoCover(FFMPEG_EXE);
        videoCover.getCover(finalVideoPath,FILE_SPACE+coverPathDB);

        //保存到数据库
        Videos video = new Videos();
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setVideoSeconds((float) videoSeconds);
        video.setVideoHeight(videoHeight);
        video.setVideoWidth(videoWidth);
        video.setVideoDesc(desc);
        video.setVideoPath(uploadPathDB);
        video.setCoverPath(coverPathDB);
        video.setStatus(VideoStatusEnum.SUCCESS.getValue());
        video.setCreateTime(new Date());

        String videoId = videoService.saveVideo(video);

        return IMoocJSONResult.ok(videoId);
    }

    /**
     * 微信真机调试无法上传多个文件
     * 此方法作废
     * @throws Exception
     */
//    @ApiOperation(value = "上传视频封面", notes = "上传视频封面的接口")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
//                    dataType = "String", paramType = "form"),
//            @ApiImplicitParam(name = "videoId", value = "视频主键id", required = true,
//                    dataType = "String", paramType = "form"),
//    })
//    @PostMapping(value = "/uploadCover", headers = "content-type=multipart/form-data")
//    public IMoocJSONResult uploadCover(String userId, String videoId,
//                                       @ApiParam(value = "短视频", required = true)
//                                               MultipartFile file) throws Exception {
//        if (StringUtils.isBlank(userId) && StringUtils.isNotBlank(videoId)) {
//            return IMoocJSONResult.errorMsg("视频id和用户id不能为空");
//        }
//
//        //保存到数据库中的相对路径
//        String uploadPathDB = "/" + userId + "/video";
//        FileOutputStream fileOutputStream = null;
//        InputStream inputStream = null;
//        String finalCoverPath = "";
//
//        try {
//            if (file != null) {
//                String fileName = file.getOriginalFilename();
//                if (StringUtils.isNotBlank(fileName)) {
//                    //文件上传的最终路径
//                    finalCoverPath = FILE_SPACE + uploadPathDB + "/" + fileName;
//                    //设置数据库保存的路径
//                    uploadPathDB += ("/" + fileName);
//
//                    File outFile=createdir(finalCoverPath);
//
//                    fileOutputStream = new FileOutputStream(outFile);
//                    inputStream = file.getInputStream();
//                    IOUtils.copy(inputStream, fileOutputStream);
//                }
//            } else {
//                return IMoocJSONResult.errorMsg("上传出错...");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return IMoocJSONResult.errorMsg("上传出错...");
//        } finally {
//            if (fileOutputStream != null) {
//                fileOutputStream.flush();
//                fileOutputStream.close();
//            }
//        }
//
//        videoService.updateVideo(videoId, uploadPathDB);
//
//        return IMoocJSONResult.ok();
//    }

    /**
     * 分页和搜索查询视频列表
     * isSaveRecord：1-需要保存
     *               0-不需要保存，或者为空
     * @param video
     * @param isSaveRecord
     * @param page
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "获取用户视频", notes = "获取用户视频的接口")
    @ApiImplicitParam(name = "page", value = "第几页", required = false)
    @PostMapping(value = "/showAll")
    public IMoocJSONResult showAll(@RequestBody Videos video,Integer isSaveRecord, Integer page)throws Exception{
        if (page==null){
            page=1;
        }
        PagedResult result=videoService.getAllVideos(video,isSaveRecord,page,PAGE_SIZE);
        return IMoocJSONResult.ok(result);
    }


    @ApiOperation(value = "我收藏(点赞)的视频", notes = "获取我收藏(点赞)的视频的接口")
    @ApiImplicitParam(name = "userId", value = "收藏用户Id", required = true,dataType = "String",paramType ="query")
    @PostMapping(value = "/showMyLike")
    public IMoocJSONResult showMyLike(String userId,Integer page, Integer pageSize)throws Exception{
        if (StringUtils.isBlank(userId)){
            return IMoocJSONResult.ok();
        }
        if (page==null){
            page=1;
        }
        if (pageSize==null){
            pageSize=5;
        }
        PagedResult result=videoService.queryMyLikeVideos(userId,page,pageSize);
        return IMoocJSONResult.ok(result);
    }

    @ApiOperation(value = "我关注的人的视频", notes = "获取我关注的人的视频的接口")
    @ApiImplicitParam(name = "userId", value = "关注的人的Id", required = true,dataType = "String",paramType ="query")
    @PostMapping(value = "/showMyFollow")
    public IMoocJSONResult showMyFollow(String userId,Integer page, Integer pageSize)throws Exception{
        if (StringUtils.isBlank(userId)){
            return IMoocJSONResult.ok();
        }
        if (page==null){
            page=1;
        }
        if (pageSize==null){
            pageSize=6;
        }
        PagedResult result=videoService.queryMyFollowVideos(userId,page,pageSize);
        return IMoocJSONResult.ok(result);
    }

    /**
     * 热搜词接口
     */
    @ApiOperation(value = "热搜词列表",notes = "获取热搜词的接口")
    @PostMapping(value = "/hot")
    public IMoocJSONResult hot(){
        return IMoocJSONResult.ok(videoService.getHotwords());
    }


    @ApiOperation(value = "点赞视频",notes = "用户喜欢点赞视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户Id",required = true,dataType = "String",paramType ="query"),
            @ApiImplicitParam(name = "videoId",value ="视频Id",required = true,dataType = "String",paramType ="query"),
            @ApiImplicitParam(name = "videoCreateId",value = "视频上传者Id",required = false,dataType = "String",paramType ="query")
    })
    @PostMapping(value = "/userLike")
    public IMoocJSONResult userLike(String userId,String videoId,String videoCreateId){
        videoService.userLikeVideo(userId,videoId,videoCreateId);
        return IMoocJSONResult.ok();
    }

    @ApiOperation(value = "取消点赞",notes = "用户取消点赞视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户Id",required = true,dataType = "String",paramType ="query"),
            @ApiImplicitParam(name = "videoId",value ="视频Id",required = true,dataType = "String",paramType ="query"),
            @ApiImplicitParam(name = "videoCreateId",value = "视频上传者Id",required = false,dataType = "String",paramType ="query")
    })
    @PostMapping(value = "/userUnLike")
    public IMoocJSONResult userUnLike(String userId,String videoId,String videoCreateId){
        videoService.userUnLikeVideo(userId,videoId,videoCreateId);
        return IMoocJSONResult.ok();
    }

    @ApiOperation(value = "保存留言",notes = "保存留言的接口")
    @PostMapping(value = "/saveComment")
    public IMoocJSONResult saveComment(@RequestBody Comments comments,
                                       String fatherCommentId,String toUserId){
        comments.setFatherCommentId(fatherCommentId);
        comments.setToUserId(toUserId);

        videoService.saveComment(comments);
        return IMoocJSONResult.ok();
    }

    @ApiOperation(value = "获取所有留言",notes = "获取所有留言的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId",value = "视频id",required = true,dataType = "String",paramType ="query" ),
            @ApiImplicitParam(name = "page",value = "页数",paramType ="query"),
            @ApiImplicitParam(name = "pageSize",value = "每页条数",paramType ="query")
    })
    @PostMapping(value = "/getVideoComments")
    public IMoocJSONResult getVideoComments(String videoId,Integer page,Integer pageSize){
        if (StringUtils.isBlank(videoId)){
            return IMoocJSONResult.ok();
        }

        //分页查询，时间倒序排序
        if (page==null){
            page=1;
        }
        if (pageSize==null){
            pageSize=10;
        }

        PagedResult list=videoService.getAllComments(videoId,page,pageSize);

        return IMoocJSONResult.ok(list);
    }





    /**
     * 创建文件夹
     */
    private File createdir(String filePathdir) {
        File file = new File(filePathdir);
        if (file.getParentFile() != null || !file.getParentFile().isDirectory()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }


}
