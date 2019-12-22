package com.leyou.upload.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.OssProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class UploadService {
    /**
     * 本地上传图片
     * @param file
     * @return
     */
    public String uploadImage(MultipartFile file) {
//        检查文件类型是否支持

//        检查上传的文件是否是图片,使用java提供过的ImageIO
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if(bufferedImage == null){
                throw  new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw  new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }

//        获取上传的源文件名
        String originalFilename = file.getOriginalFilename();
//        获取文件的后缀  .png .jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
//        创建新的文件名  uuid + 后缀
        String newName = UUID.randomUUID().toString()+suffix;
//        检查文件存放目录是否可用
        String filePath = "C:\\workspace\\heima-jee109\\nginx-1.12.2\\html";
        File dir = new File(filePath);
//        如果不可用要创建
        if(!dir.exists()){
            dir.mkdir();
        }
//        把图片上传到 目录
        try {
            file.transferTo(new File(dir,newName));
        } catch (IOException e) {
            e.printStackTrace();
            log.error("上传图片失败！！");
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }
//        把新图片的地址返回
        String imgUrl = "http://image.leyou.com/"+newName;
        return imgUrl;
    }

    @Autowired
    private OSS client;
    @Autowired
    private OssProperties prop;
    /**
     * 生成oss的签名
     * @return
     */
    public Map<String, Object> signature() {
        try {
            long expireTime = prop.getExpireTime();
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, prop.getMaxFileSize());
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, prop.getDir());

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, Object> respMap = new LinkedHashMap<>();
            respMap.put("accessId", prop.getAccessKeyId());
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", prop.getDir());
            respMap.put("host", prop.getHost());
            respMap.put("expire", expireEndTime);
            return respMap;
        }catch (Exception e){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }
}
