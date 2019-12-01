package notreddit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@EnableSpringDataWebSupport
@SpringBootApplication
public class NotRedditApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotRedditApplication.class, args);
    }
}
