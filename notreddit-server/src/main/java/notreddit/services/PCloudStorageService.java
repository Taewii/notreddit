package notreddit.services;

import com.pcloud.sdk.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@ConfigurationProperties(prefix = "app.pcloud")
public class PCloudStorageService implements CloudStorage {

    private String accessToken;
    private final ApiClient api;

    public PCloudStorageService() {
        this.api = PCloudSdk.newClientBuilder()
                .authenticator(Authenticators.newOAuthAuthenticator(getAccessToken()))
                .create();
    }

    @Override
    public Map<String, Object> uploadFileAndGetParams(MultipartFile file) {
        Map<String, Object> params = new HashMap<>();
        RemoteFile uploadedFile;

        try {
            uploadedFile = uploadFile(file);
        } catch (IOException | ApiError e) {
            log.error("File uploading failed.");
            e.printStackTrace();
            return params;
        }

        try {
            String urlPath = uploadedFile
                    .createFileLink()
                    .bestUrl()
                    .getPath();

            params.put("id", uploadedFile.fileId());
            params.put("url", urlPath);
        } catch (IOException | ApiError e) {
            log.error("URL creation failed.");
            e.printStackTrace();
        }

        return params;
    }

    @Override
    public Map<String, Object> updateFile(MultipartFile newFile, String oldFileId) {
        removeFile(oldFileId);
        return uploadFileAndGetParams(newFile);
    }

    @Override
    public boolean removeFile(String fileId) {
        try {
            return api.deleteFile(Long.parseLong(fileId)).execute();
        } catch (IOException | ApiError e) {
            log.error("File deletion failed.");
            e.printStackTrace();
            return false;
        }
    }

    private RemoteFile uploadFile(MultipartFile multipartFile) throws IOException, ApiError {
        File file = File.createTempFile("temp", "tmp");
        multipartFile.transferTo(file);

        return api.createFile(
                RemoteFolder.ROOT_FOLDER_ID,
                file.getName(),
                DataSource.create(file))
                .execute();
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
