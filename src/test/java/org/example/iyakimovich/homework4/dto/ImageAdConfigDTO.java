package org.example.iyakimovich.homework4.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ImageAdConfigDTO {
    private String[] safeFlags;
    private String[] highRiskFlags;
    private String[] unsafeFlags;
    private String[] wallUnsafeFlags;
    private boolean showsAds;
}
