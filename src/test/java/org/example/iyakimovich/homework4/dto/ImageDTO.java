package org.example.iyakimovich.homework4.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

@Data
public class ImageDTO {
    @JsonProperty("data")
    private ImageDataDTO imageData;

    private boolean success;
    private int status;
}
