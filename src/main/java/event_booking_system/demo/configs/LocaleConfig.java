package event_booking_system.demo.configs;

// i18n support

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocaleConfig extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {
    List<Locale> LOCALES = List.of(
            Locale.of("en"),
            Locale.of("vi")
    );

    @NotNull
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String headerLang = request.getHeader("Accept-Language");
        return StringUtils.hasLength(headerLang)
                ? Locale.lookup(Locale.LanguageRange.parse(headerLang), LOCALES)
                : Locale.getDefault();
    }

    @Bean
    public MessageSource messageSource(
            @Value("${spring.messages.basename}") String baseName,
            @Value("${spring.messages.encoding}") String encoding,
            @Value("${spring.messages.default-locate}") String defaultLocale,
            @Value("${spring.messages.cache-duration}") int cacheDurations
    ) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        messageSource.setDefaultEncoding(encoding);
        messageSource.setDefaultLocale(Locale.of(defaultLocale));
        messageSource.setCacheSeconds(cacheDurations);
        return messageSource;
    }
}
