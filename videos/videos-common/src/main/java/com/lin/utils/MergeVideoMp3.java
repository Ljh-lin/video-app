package com.lin.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MergeVideoMp3 {

    private String ffmpegEXE;

    public MergeVideoMp3(String ffmpegEXE){
        super();
        this.ffmpegEXE=ffmpegEXE;
    }

    public void convertor(String videoInputPath,String mp3InputPath,
                          double seconds,String videoOutputPath)throws Exception{

        //ffmpeg.exe -i bgm.mp3 -i 苏州大裤衩.mp4 -t 7 -y 新的视频.mp4
        List<String> command=new ArrayList<>();
        command.add(ffmpegEXE);

        command.add("-i");
        command.add(mp3InputPath);

        command.add("-i");
        command.add(videoInputPath);

        command.add("-t");
        command.add(String.valueOf(seconds));

        command.add("-y");
        command.add(videoOutputPath);

        ProcessBuilder builder=new ProcessBuilder(command);
        Process process=builder.start();

        //释放内存
        InputStream errorStream=process.getErrorStream();
        InputStreamReader inputStreamReader=new InputStreamReader(errorStream);
        BufferedReader br=new BufferedReader(inputStreamReader);
        String line="";
        while ((line=br.readLine())!=null){
        }
        if (br!=null){br.close();}
        if (inputStreamReader!=null){inputStreamReader.close();}
        if (errorStream!=null){errorStream.close();}
    }

    public static void main(String[] args) {
        MergeVideoMp3 ffepeg=new MergeVideoMp3("D:\\Download\\ffmpeg\\bin\\ffmpeg.exe");

        try {
            ffepeg.convertor("D:\\IDEA\\videos-dev-resource\\柯基.mp4",
                    "D:\\IDEA\\videos-dev-resource\\bgm\\1222.mp3",23.5,
                    "D:\\IDEA\\videos-dev-resource\\testvideo\\新柯基.mp4");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
