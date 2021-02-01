package cn.coderap.config.center;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by yw
 * 2021/1/25
 */
@Component
@ConfigurationProperties(prefix = "file")
//@PropertySource("classpath:file-upload-dev.properties")
@PropertySource("classpath:file-upload-prod.properties")
public class FileUpload {

    private String userFaceImageLocation;
    private String imageServerUrl;

    public String getUserFaceImageLocation() {
        return userFaceImageLocation;
    }

    public void setUserFaceImageLocation(String userFaceImageLocation) {
        this.userFaceImageLocation = userFaceImageLocation;
    }

    public String getImageServerUrl() {
        return imageServerUrl;
    }

    public void setImageServerUrl(String imageServerUrl) {
        this.imageServerUrl = imageServerUrl;
    }
}
