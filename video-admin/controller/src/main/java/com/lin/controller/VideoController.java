package com.lin.controller;

import com.lin.enums.VideoStatusEnum;
import com.lin.pojo.Bgm;
import com.lin.service.VideoService;
import com.lin.utils.MyJSONResult;
import com.lin.utils.PagedResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/video")
public class VideoController{

    @Autowired
    private VideoService videoService;


    @RequestMapping("/showAddBgm")
    public String hell(){
        return "video/addBgm";
    }

    @PostMapping("/addBgm")
    @ResponseBody
    public MyJSONResult addBgm(Bgm bgm){
        videoService.addBgm(bgm);
        return MyJSONResult.ok();
    }

    @GetMapping("/showBgmList")
    public String bgmList(){
        return "video/bgmList";
    }

    @PostMapping("/queryBgmList")
    @ResponseBody
    public PagedResult queryBgmList(Integer page){
        return videoService.queryBgmList(page,10);
    }

    @PostMapping("/delBgm")
    @ResponseBody
    public MyJSONResult delBgm(String bgmId){
        videoService.deleteBgm(bgmId);
        return MyJSONResult.ok();
    }

    @GetMapping("/showReportList")
    public String showReportList(){
        return "video/reportList";
    }

    @PostMapping("/reportList")
    @ResponseBody
    public PagedResult reportList(Integer page){
        PagedResult result=videoService.queryReportList(page,10);
        return result;
    }

    @PostMapping("/forbidVideo")
    @ResponseBody
    public MyJSONResult forbidVideo(String videoId){
        videoService.updateVideoStatus(videoId, VideoStatusEnum.FORBID.value);
        return MyJSONResult.ok();
    }


    @PostMapping("/bgmUpload")
    @ResponseBody
    public MyJSONResult bgmUpload(@RequestParam("file") MultipartFile[] files) throws IOException {
        String fileSpace = "D:" + File.separator + "IDEA"+File.separator +"videos-dev-resource" + File.separator + "admin-bgm";
        // 保存到数据库中的相对路径
        String uploadPathDB = File.separator + "bgm";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (files != null && files.length > 0) {

                String fileName = files[0].getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    // 文件上传的最终保存路径
                    String finalPath = fileSpace + uploadPathDB + File.separator + fileName;
                    // 设置数据库保存的路径
                    uploadPathDB += (File.separator + fileName);

                    File outFile = new File(finalPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }

            } else {
                return MyJSONResult.errorMsg("上传出错...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return MyJSONResult.errorMsg("上传出错...");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        return MyJSONResult.ok(uploadPathDB);
    }


}
