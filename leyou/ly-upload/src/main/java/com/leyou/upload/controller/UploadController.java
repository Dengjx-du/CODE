package com.leyou.upload.controller;

import com.leyou.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 本地上传图片
     * @param file
     * @return
     */
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam(name = "file") MultipartFile file){
        return ResponseEntity.ok(uploadService.uploadImage(file));
    }

    /**
     * 生成oss的签名
     * @return
     */
    @GetMapping("/signature")
    public ResponseEntity<Map<String,Object>> signature(){
        return ResponseEntity.ok(uploadService.signature());
    }
}
