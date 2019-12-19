package notreddit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import notreddit.data.entities.Role;
import notreddit.data.entities.Subreddit;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;

import java.time.LocalDateTime;
import java.time.ZoneId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SingletonModelMapper {

    public static ModelMapper mapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setAmbiguityIgnored(true);

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
}
