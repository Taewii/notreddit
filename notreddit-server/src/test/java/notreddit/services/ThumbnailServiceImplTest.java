package notreddit.services;

import notreddit.services.implementations.ThumbnailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

class ThumbnailServiceImplTest {

    private static final String API_BASE_URL = "https://api.screenshotmachine.com/?";
    private static final String PDF_API_BASE_URL = "https://pdfapi.screenshotmachine.com/?";

    private String url = "https://random-url.com";
    private ThumbnailService thumbnailService;

    @BeforeEach
    public void setUp() {
        thumbnailService = new ThumbnailServiceImpl();
    }

    @Test
    void generateThumbnailUrl() {
        String url = thumbnailService.generateThumbnailUrl(this.url);
        assertEquals(API_BASE_URL + "key=null&url=https%3A%2F%2Frandom-url.com", url);
    }

    @Test
    void generateScreenshotApiUrl() throws UnsupportedEncodingException {
        Map<String, String> options = new HashMap<>();
        options.put("url", url);
        options.put("title", "title");


        String expected = PDF_API_BASE_URL + "key=null&title=title&url=https%3A%2F%2Frandom-url.com";
        String url = thumbnailService.generatePdfApiUrl(options);
        assertEquals(expected, url);
    }

    @Test
    void generatePdfApiUrl() throws UnsupportedEncodingException {
        Map<String, String> options = new HashMap<>();
        options.put("url", url);
        options.put("title", "title");


        String expected = API_BASE_URL + "key=null&title=title&url=https%3A%2F%2Frandom-url.com";
        String url = thumbnailService.generateScreenshotApiUrl(options);
        assertEquals(expected, url);
    }
}