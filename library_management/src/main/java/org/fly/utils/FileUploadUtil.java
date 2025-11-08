//package org.fly.utils;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//
//@Slf4j
//@Component // 将类注册为 Spring Bean
//public class FileUploadUtil {
//
//    @Value("${upload.path:E:/develop/idea/workspace/fly_personal_develop/library_web_management/coverImage/}")
//    private String uploadPath;
//
//    public String saveFile(MultipartFile file, String fileName) throws IOException {
//        // 获取上传目录（例如：./src/main/resources/static/coverImage）
//        File dir = new File(uploadPath);
//        if (!dir.exists()) {
//            dir.mkdirs(); // 创建目录
//        }
//
//        // 构造完整文件路径
//        String filePath = uploadPath + "/" + fileName;
//        file.transferTo(new File(filePath));
//
//        // 返回文件名，不包含 /coverImage/
//        log.info("文件保存成功，文件路径：{}", filePath);
//        return fileName; // 仅返回文件名，如 "123.jpg"
//    }
//}
