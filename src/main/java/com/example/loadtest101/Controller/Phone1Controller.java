package com.example.loadtest101.Controller;

import com.example.loadtest101.Response.PhoneResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@RestController
public class Phone1Controller {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    public Phone1Controller(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder
                .rootUri("http://localhost:8081")
                .build();
        this.mapper = objectMapper;
    }

    @GetMapping("/phone/{id}")
    public ResponseEntity<PhoneResponse> getDefaultPhone(@PathVariable String id) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/phone?number={id}", String.class, id);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            try {
                PhoneResponse response = mapper.readValue(responseEntity.getBody().replaceAll("\r", ""), PhoneResponse.class);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.status(responseEntity.getStatusCode()).build();
        }
    }

    @Async("myCustomExecutor")
    @GetMapping("/phoneAsync/{id}")
    public CompletableFuture<PhoneResponse> getAsyncPhone(@PathVariable String id) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/phone?number={id}", String.class, id);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            try {
                PhoneResponse response = mapper.readValue(responseEntity.getBody().replaceAll("\r", ""), PhoneResponse.class);
                return CompletableFuture.completedFuture(response);
            } catch (Exception e) {
                return CompletableFuture.failedFuture(new RuntimeException("Error parsing response", e));
            }
        } else {
            return CompletableFuture.failedFuture(new RuntimeException("Error from downstream service"));
        }
    }
}
