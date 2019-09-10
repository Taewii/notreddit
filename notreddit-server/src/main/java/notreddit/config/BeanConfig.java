package notreddit.config;

import notreddit.domain.entities.Role;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        Converter<Role, String> toAuthorityString = new AbstractConverter<>() {
            @Override
            protected String convert(Role role) {
                return role == null ? null : role.getAuthority().substring("ROLE_".length());
            }
        };

        mapper.addConverter(toAuthorityString);
        return mapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
