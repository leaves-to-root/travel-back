package com.travel.config;

import com.travel.properties.ALiYunOssProperty;
import com.travel.utils.ALiYunOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ALiYunOssConfig {

    @Bean
    @ConditionalOnMissingBean
    public ALiYunOssUtil aliOssUtil(ALiYunOssProperty aLiYunOssProperties) {
        log.info("开始创建AliOssUtil对象，Oss配置：{}", aLiYunOssProperties);

        return new ALiYunOssUtil(aLiYunOssProperties.getEndpoint(),
                aLiYunOssProperties.getAccessKeyId(),
                aLiYunOssProperties.getAccessKeySecret(),
                aLiYunOssProperties.getBucketName());
    }
}
