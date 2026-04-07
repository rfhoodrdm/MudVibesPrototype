package com.example.mudvibe.uiview;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MudUiController {

    @GetMapping({"/", "/index"})
    public String landing() {
        return "index";
    }

    @GetMapping("/client")
    public String client() {
        return "mud-client";
    }

    @GetMapping("/rules")
    public String rulesIndex() {
        return "rules/index";
    }

    @GetMapping("/rules/overview")
    public String rulesOverview() {
        return "rules/overview";
    }

    @GetMapping("/rules/etiquette")
    public String rulesEtiquette() {
        return "rules/etiquette";
    }
}
