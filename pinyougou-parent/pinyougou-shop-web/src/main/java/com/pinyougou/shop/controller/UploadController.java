package com.pinyougou.shop.controller;

import com.pinyougou.untils.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;

@RestController

public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;//文件服务器地址

    @RequestMapping("upload")
    public Result upload(MultipartFile file){
        //获取文件的扩展名
        String filename = file.getOriginalFilename();
        String substring = filename.substring(filename.lastIndexOf("." )+1);

        try {
            //创建FirstDFS客户端对象
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            //执行上传处理
            String path = fastDFSClient.uploadFile(file.getBytes(), substring);
            //接收返回的URL地址，并和IP地址拼接为完整的URL路径
            String url = FILE_SERVER_URL + path;

            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败！");
        }

    }


}
