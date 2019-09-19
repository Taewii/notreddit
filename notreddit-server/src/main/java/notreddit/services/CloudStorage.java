package notreddit.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudStorage {

    Map<String, Object> uploadFileAndGetParams(MultipartFile file);

    Map<String, Object> updateFile(MultipartFile newFile, String oldFileId);

    boolean removeFile(String fileId);
}
