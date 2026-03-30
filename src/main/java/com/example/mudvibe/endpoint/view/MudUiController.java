package com.example.mudvibe.endpoint.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MudUiController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
