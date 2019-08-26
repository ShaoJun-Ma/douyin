package com.msj.douyin.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FFMpegUtil {
    private static String ffmpegEXE = "D:\\software\\ffmpeg4.2\\bin\\ffmpeg.exe";

    //mp4转avi
    public static void convertor(String videoInputPath,String mp3InputPath,
                          double seconds,String videoOutputPath) throws IOException {
        //ffmpeg.exe -i videoInput.mp4 -i mp3Input.mp3 -map 0:v:0 -map 1:a:0 -t 6 -y videoOutput.mp4
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(videoInputPath);
        command.add("-i");
        command.add(mp3InputPath);
        command.add("-map");
        command.add("0:v:0");
        command.add("-map");
        command.add("1:a:0");
        command.add("-t");
        command.add(String.valueOf(seconds));
        command.add("-y");
        command.add(videoOutputPath);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ((line = br.readLine()) != null){
        }

        if(br!=null){
            br.close();
        }
        if(inputStreamReader!=null){
            inputStreamReader.close();
        }
        if(errorStream != null){
            errorStream.close();
        }
    }

    /**
     * 截取视频封面
     * @param videoPath  视频路径
     * @param imgPath  封面存放路径
     * @return
     */
    public static boolean processImg(String videoPath,String imgPath){
        File file = new File(videoPath);
        if (!file.exists()) {
            System.err.println("路径[" + videoPath + "]对应的视频文件不存在!");
            return false;
        }
        List<String> commands = new ArrayList<String>();
        commands.add(ffmpegEXE);
        commands.add("-i");
        commands.add(videoPath);
        commands.add("-y");
        commands.add("-f");
        commands.add("image2");
        commands.add("-ss");
        commands.add("0");//这个参数是设置截取视频多少秒时的画面
        //commands.add("-t");
        //commands.add("0.001");
        commands.add("-s");
        commands.add("680x900");
        commands.add(imgPath);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            builder.start();
            System.out.println("截取成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
