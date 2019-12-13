package notreddit.services;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class DropboxService implements CloudStorage {

    private static final String RAW_TYPE_POSTFIX = "&raw=1";

    private final DbxClientV2 client;

    @Autowired
    public DropboxService(@Value("${app.dropbox.access.token}") String ACCESS_TOKEN) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/notreddit").build();
        client = new DbxClientV2(config, ACCESS_TOKEN);
    }

    public DropboxService(DbxClientV2 client) {
        this.client = client;
    }

    @Override
    public Map<String, Object> uploadFileAndGetParams(MultipartFile file) {
        String imagePath = "/" + UUID.randomUUID().toString().substring(24) + file.getOriginalFilename();
        uploadFile(file, imagePath);
        log.info("File upload successful.");

        SharedLinkMetadata sharedLinkWithSettings = createSharedLinkFromPath(imagePath);

        Map<String, Object> params = new HashMap<>();
        params.put("url", sharedLinkWithSettings.getUrl() + RAW_TYPE_POSTFIX);
        params.put("contentType", file.getContentType());
        return params;
    }

    @Override
    public Map<String, Object> updateFile(MultipartFile newFile, String oldFileId) {
        removeFile(oldFileId);
        return uploadFileAndGetParams(newFile);
    }

    @Override
    public boolean removeFile(String fileId) {
        SharedLinkMetadata sharedLinkMetadata;
        try {
            sharedLinkMetadata = getSharedLinkMetadata(fileId);

            if (sharedLinkMetadata == null) return false;
            client.files().deleteV2(sharedLinkMetadata.getPathLower());
            return true;
        } catch (DbxException e) {
            log.error("File deletion failed.");
            e.printStackTrace();
        }
        return false;
    }

    private FileMetadata uploadFile(MultipartFile file, String imagePath) {
        InputStream in;
        try {
            in = file.getInputStream();
            return client.files().uploadBuilder(imagePath).uploadAndFinish(in);
        } catch (IOException | DbxException e) {
            log.error("File upload failed.");
            e.printStackTrace();
        }
        return null;
    }

    private SharedLinkMetadata createSharedLinkFromPath(String path) {
        try {
            return client.sharing().createSharedLinkWithSettings(path);
        } catch (DbxException e) {
            log.error("Creating shared link failed.");
            e.printStackTrace();
        }
        return null;
    }

    private SharedLinkMetadata getSharedLinkMetadata(String url) {
        try {
            return client.sharing().getSharedLinkMetadata(url);
        } catch (DbxException e) {
            log.error("Retrieving shared link failed.");
            e.printStackTrace();
        }
        return null;
    }
}
