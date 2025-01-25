package com.manav.dockapimainserver;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class MainController {

    @GetMapping("")
    public String home() {
        return "index";
    }
    @PostMapping("/upload-project")
    public String uploadProject(ProjectRequestBody projectRequestBody) {
    return "Project uploaded successfully";
    }
}
