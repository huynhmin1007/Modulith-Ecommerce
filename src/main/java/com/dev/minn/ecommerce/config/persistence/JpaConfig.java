package com.dev.minn.ecommerce.config.persistence;

import com.dev.minn.ecommerce.common.utils.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {

    @Bean
    AuditorAware<String> auditorProvider() {
        return () -> {
            String id = SecurityUtils.getCurrentUserId();

            if (id == null)
                return Optional.of("SYSTEM");

            return Optional.of(id);
        };
    }
}
