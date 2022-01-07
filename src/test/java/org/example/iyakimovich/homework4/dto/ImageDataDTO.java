package org.example.iyakimovich.homework4.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ImageDataDTO {
    private String id;
    private String title;
    private String description;
    private long datetime;
    private String type;
    private boolean animated;
    private int width;
    private int height;
    private int size;
    private int views;
    private long bandwidth;
    private String vote;
    private boolean favorite;
    private boolean nsfw;
    private String section;

    @JsonProperty("account_url")
    private String sectionURL;

    @JsonProperty("account_id")
    private int accountID;

    @JsonProperty("is_ad")
    private boolean isAD;

    @JsonProperty("in_most_viral")
    private boolean inMostViral;

    @JsonProperty("has_sound")
    private boolean hasSound;

    private String[] tags;
    private String ad_type;
    private String ad_url;
    private String edited;
    private String in_gallery;
    private String deletehash;

    private String name;
    private String link;

    @JsonProperty("ad_config")
    private ImageAdConfigDTO adConfig;

}
