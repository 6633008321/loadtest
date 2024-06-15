package com.example.loadtest101.Response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PhoneResponse {
    @JsonProperty("error")
    private String error;

    @JsonProperty("name")
    private String name;
}
