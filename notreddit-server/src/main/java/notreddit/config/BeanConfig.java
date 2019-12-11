package notreddit.config;

import notreddit.domain.entities.Role;
import notreddit.domain.entities.Subreddit;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Configuration
public class BeanConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setAmbiguityIgnored(true);

        // if I do em with lambda functions it breaks...
        Converter<LocalDateTime, Long> toInstant = new Converter<LocalDateTime, Long>() {
            public Long convert(MappingContext<LocalDateTime, Long> context) {
                return context.getSource() == null ? null : context.getSource()
                        .atZone(ZoneId.of("Europe/Sofia"))
                        .toInstant()
                        .getEpochSecond();
            }
        };

        Converter<Subreddit, String> toSubredditTitle = new Converter<Subreddit, String>() {
            public String convert(MappingContext<Subreddit, String> context) {
                return context.getSource() == null ? null : context.getSource().getTitle();
            }
        };

        Converter<Role, String> toAuthorityString = new Converter<Role, String>() {
            public String convert(MappingContext<Role, String> context) {
                return context.getSource() == null ? null : context.getSource()
                        .getAuthority().substring("ROLE_".length());
            }
        };

        mapper.addConverter(toInstant);
        mapper.addConverter(toSubredditTitle);
        mapper.addConverter(toAuthorityString);
        return mapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
