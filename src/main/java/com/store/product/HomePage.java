package com.store.product;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/home")
@RestController
public class HomePage {
	
	@GetMapping("/test")
	public String testPage() {
		return "Wel-Come";
	}
	
	@GetMapping
	public ResponseEntity<Resource> welcomePage() {
		Resource resource = new ClassPathResource("templates/Home.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
		
	}

}
