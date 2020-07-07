package io.github.holovid.server.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NotNull final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/data/**")
                .addResourceLocations("/data/");
    }
}
