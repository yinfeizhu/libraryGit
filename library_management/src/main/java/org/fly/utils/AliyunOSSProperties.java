package org.fly.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")// 指定配置文件的前缀
public class AliyunOSSProperties {
    private String endpoint;// 阿里云OSS的endpoint
    private String bucketName;// 阿里云OSS的bucketName
    private String region;// 阿里云OSS的区域
}
