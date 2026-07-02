package com.travel.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.travel.common.Result;
import com.travel.utils.ALiYunOssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
@Tag(name = "通用接口", description = "文件上传等公共功能")
public class CommonController {

    private final ALiYunOssUtil aliYunOssUtil;

    @Value("${travel.aliyun.oss.endpoint:}")
    private String ossEndpoint;

    @PostMapping("/upload")
    @Operation(summary = "文件上传（优先 OSS，未配置时存本地）")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return Result.error("请选择文件");
        try {
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String newName = IdUtil.fastSimpleUUID() + ext;

            // 如果 OSS 已配置则上传 OSS
            if (StrUtil.isNotBlank(ossEndpoint)) {
                String objectName = "travel/" + newName;
                String url = aliYunOssUtil.upload(file.getBytes(), objectName);
                return Result.success(Map.of("url", url));
            }

            // 否则存本地
            String uploadDir = System.getProperty("user.dir") + "/uploads/avatars/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            File dest = new File(uploadDir + newName);
            file.transferTo(dest);
            String url = "/uploads/avatars/" + newName;
            log.info("文件已保存到本地: {}", dest.getAbsolutePath());
            return Result.success(Map.of("url", url));
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败");
        }
    }
}
