package notreddit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import notreddit.domain.entities.Role;
import notreddit.domain.entities.Subreddit;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.ZoneId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SingletonModelMapper {

    public static ModelMapper mapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setAmbiguityIgnored(true);

        Converter<LocalDateTime, Long> toInstant = new AbstractConverter<>() {
            @Override
            protected Long convert(LocalDateTime localDateTime) {
                return localDateTime == null ? null : localDateTime
                        .atZone(ZoneId.of("Europe/Sofia"))
                        .toInstant()
                        .getEpochSecond();
            }
        };

        Converter<Subreddit, String> toSubredditTitle = new AbstractConverter<>() {
            @Override
            protected String convert(Subreddit role) {
                return role == null ? null : role.getTitle();
            }
        };

        Converter<Role, String> toAuthorityString = new AbstractConverter<>() {
            @Override
            protected String convert(Role role) {
                return role == null ? null : role.getAuthority().substring("ROLE_".length());
            }
        };

        mapper.addConverter(toInstant);
        mapper.addConverter(toSubredditTitle);
        mapper.addConverter(toAuthorityString);

        return mapper;
    }
}
