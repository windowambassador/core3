package com.example.core3.controller;

import com.example.core3.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final AppProperties appProperties;

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/tasks";
        }
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String homePage(Authentication authentication, Model model) {
        model.addAttribute("appName", appProperties.getName());
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
        }
        return "home";
    }
}
