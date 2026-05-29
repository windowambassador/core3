package com.example.core3.config;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final AppProperties appProperties;

    @ModelAttribute("appName")
    public String appName() {
        return appProperties.getName();
    }
}
