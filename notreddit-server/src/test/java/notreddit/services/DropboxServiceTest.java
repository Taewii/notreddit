package notreddit.services;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DbxUserFilesRequests;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadBuilder;
import com.dropbox.core.v2.sharing.DbxUserSharingRequests;
import com.dropbox.core.v2.sharing.LinkPermissions;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import notreddit.services.implementations.DropboxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DropboxServiceTest {

    private DbxClientV2 client;
    private CloudStorage dropboxService;

    @BeforeEach
    void setUp() {
        client = Mockito.mock(DbxClientV2.class, RETURNS_DEEP_STUBS);
        dropboxService = new DropboxService(client);
    }

    @Test
    void uploadFileAndGetParams_shouldInvokeAllTheMethodsAndReturnTheCorrectData() throws IOException, DbxException {
        DbxUserSharingRequests sharing = mock(DbxUserSharingRequests.class, RETURNS_DEEP_STUBS);
        DbxUserFilesRequests files = mock(DbxUserFilesRequests.class, RETURNS_DEEP_STUBS);
        when(client.sharing()).thenReturn(sharing);
        when(client.files()).thenReturn(files);

        InputStream inputStream = new ByteArrayInputStream("test".getBytes());
        MultipartFile file = new MockMultipartFile("name", "originalName", "image/jpeg", inputStream);
        String path = "/" + file.getOriginalFilename();

        FileMetadata fileMetadata =
                new FileMetadata("name", "id", new Date(), new Date(), "123456789", 1L);

        UploadBuilder uploadBuilder = mock(UploadBuilder.class, RETURNS_DEEP_STUBS);
        when(files.uploadBuilder(any(String.class))).thenReturn(uploadBuilder);
        when(uploadBuilder.uploadAndFinish(inputStream)).thenReturn(fileMetadata);

        SharedLinkMetadata sharedLinkMetadata = SharedLinkMetadata.
                newBuilder("url", "name", LinkPermissions.newBuilder(true).build())
                .withPathLower("path")
                .build();

        when(sharing.createSharedLinkWithSettings(path)).thenReturn(sharedLinkMetadata);

        Map<String, Object> response = dropboxService.uploadFileAndGetParams(file);

        verify(files).uploadBuilder(any(String.class));
        verify(uploadBuilder).uploadAndFinish(any(ByteArrayInputStream.class));
        verify(sharing).createSharedLinkWithSettings(any(String.class));

        assertEquals(2, response.size());
        assertEquals("image/jpeg", response.get("contentType"));
        assertEquals("null&raw=1", response.get("url"));
    }

    @Test
    void updateFile() throws IOException, DbxException {
        DbxUserSharingRequests sharing = mock(DbxUserSharingRequests.class, RETURNS_DEEP_STUBS);
        DbxUserFilesRequests files = mock(DbxUserFilesRequests.class, RETURNS_DEEP_STUBS);
        when(client.sharing()).thenReturn(sharing);
        when(client.files()).thenReturn(files);

        InputStream inputStream = new ByteArrayInputStream("test".getBytes());
        MultipartFile file = new MockMultipartFile("name", "originalName", "image/jpeg", inputStream);
        String path = "/" + file.getOriginalFilename();

        FileMetadata fileMetadata =
                new FileMetadata("name", "id", new Date(), new Date(), "123456789", 1L);

        UploadBuilder uploadBuilder = mock(UploadBuilder.class, RETURNS_DEEP_STUBS);
        when(files.uploadBuilder(any(String.class))).thenReturn(uploadBuilder);
        when(uploadBuilder.uploadAndFinish(inputStream)).thenReturn(fileMetadata);

        SharedLinkMetadata sharedLinkMetadata = SharedLinkMetadata.
                newBuilder("url", "name", LinkPermissions.newBuilder(true).build())
                .withPathLower("path")
                .build();

        when(sharing.createSharedLinkWithSettings(path)).thenReturn(sharedLinkMetadata);

        Map<String, Object> response = dropboxService.updateFile(file, "fileId");

        verify(files).uploadBuilder(any(String.class));
        verify(uploadBuilder).uploadAndFinish(any(ByteArrayInputStream.class));
        verify(sharing).createSharedLinkWithSettings(any(String.class));

        assertEquals(2, response.size());
        assertEquals("image/jpeg", response.get("contentType"));
        assertEquals("null&raw=1", response.get("url"));
    }

    @Test
    void removeFile_withExistingSharedLinkMetadata_removesFileAndReturnsTrue() throws DbxException {
        DbxUserSharingRequests sharing = mock(DbxUserSharingRequests.class);
        DbxUserFilesRequests files = mock(DbxUserFilesRequests.class);
        when(client.sharing()).thenReturn(sharing);
        when(client.files()).thenReturn(files);

        SharedLinkMetadata sharedLinkMetadata = SharedLinkMetadata.
                newBuilder("url", "name", LinkPermissions.newBuilder(true).build())
                .withPathLower("path")
                .build();


        when(sharing.getSharedLinkMetadata(any(String.class))).thenReturn(sharedLinkMetadata);

        boolean response = dropboxService.removeFile("fileId");

        verify(sharing).getSharedLinkMetadata(any(String.class));
        verify(files).deleteV2(sharedLinkMetadata.getPathLower());
        assertTrue(response);
    }

    @Test
    void removeFile_withNonExistingSharedLinkMetadata_returnsFalse() throws DbxException {
        DbxUserSharingRequests sharing = mock(DbxUserSharingRequests.class);
        DbxUserFilesRequests files = mock(DbxUserFilesRequests.class);

        when(client.sharing()).thenReturn(sharing);
        when(client.files()).thenReturn(files);

        when(sharing.getSharedLinkMetadata(any(String.class))).thenReturn(null);

        boolean response = dropboxService.removeFile("fileId");

        verify(sharing).getSharedLinkMetadata(any(String.class));
        verify(files, never()).deleteV2(any());
        assertFalse(response);
    }

    @Test
    void removeFile_withThrownDbxException_returnsFalse() {
        DbxUserSharingRequests sharing = mock(DbxUserSharingRequests.class);
        DbxUserFilesRequests files = mock(DbxUserFilesRequests.class);
        when(client.sharing()).thenReturn(sharing);
        when(client.files()).thenReturn(files);

        SharedLinkMetadata sharedLinkMetadata = SharedLinkMetadata.
                newBuilder("url", "name", LinkPermissions.newBuilder(true).build())
                .withPathLower("path")
                .build();

        try {
            when(sharing.getSharedLinkMetadata(any(String.class))).thenReturn(null);
        } catch (DbxException e) {
            e.printStackTrace();
        }

        boolean result = dropboxService.removeFile("fileId");

        try {
            verify(sharing).getSharedLinkMetadata(any());
            verify(files, never()).deleteV2(sharedLinkMetadata.getPathLower());
            assertFalse(result);
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }
}