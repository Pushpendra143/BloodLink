package com.example.bloodlink.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.bloodlink.model.BloodGroup;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // lets path variables / query params use "O+" as well as "O_POS"
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, BloodGroup.class, BloodGroup::from);
    }
}
