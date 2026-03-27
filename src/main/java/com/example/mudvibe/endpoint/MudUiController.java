package com.example.mudvibe.endpoint;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MudUiController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
