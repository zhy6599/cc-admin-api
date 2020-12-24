package cc.admin.config.oss;

import cc.admin.common.util.oss.OssBootUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssBootConfiguration {

    @Value("${cc.admin.oss.endpoint}")
    private String endpoint;
    @Value("${cc.admin.oss.accessKey}")
    private String accessKeyId;
    @Value("${cc.admin.oss.secretKey}")
    private String accessKeySecret;
    @Value("${cc.admin.oss.bucketName}")
    private String bucketName;
    @Value("${cc.admin.oss.staticDomain}")
    private String staticDomain;


    @Bean
    public void initOssBootConfiguration() {
        OssBootUtil.setEndPoint(endpoint);
        OssBootUtil.setAccessKeyId(accessKeyId);
        OssBootUtil.setAccessKeySecret(accessKeySecret);
        OssBootUtil.setBucketName(bucketName);
        OssBootUtil.setStaticDomain(staticDomain);
    }
}
