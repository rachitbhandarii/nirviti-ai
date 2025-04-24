package com.nirviti.ai;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class HomeController {
    @GetMapping("/home")
    public ResponseEntity<String> processContent() {
        return ResponseEntity.ok("home");
    }
}
