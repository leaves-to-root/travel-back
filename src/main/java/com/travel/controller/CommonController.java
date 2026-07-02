package com.travel.controller;

import cn.hutool.core.util.IdUtil;
import com.travel.common.Result;
import com.travel.utils.ALiYunOssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
@Tag(name = "通用接口", description = "文件上传等公共功能")
public class CommonController {

    private final ALiYunOssUtil aliYunOssUtil;

    @PostMapping("/upload")
    @Operation(summary = "文件上传（OSS）")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return Result.error("请选择文件");
        try {
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String objectName = "travel/" + IdUtil.fastSimpleUUID() + ext;
            String url = aliYunOssUtil.upload(file.getBytes(), objectName);
            return Result.success(Map.of("url", url));
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败");
        }
    }
}
