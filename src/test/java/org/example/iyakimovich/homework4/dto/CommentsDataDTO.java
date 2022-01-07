package org.example.iyakimovich.homework4.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class CommentsDataDTO {
    private long id;

    @JsonProperty("image_id")
    private String imageId;

    private String comment;
    private String author;

    @JsonProperty("author_id")
    private String authorId;

    @JsonProperty("on_album")
    private boolean onAlbum;

    @JsonProperty("album_cover")
    private String albumCover;

    private int ups;
    private int downs;
    private int points;
    private long datetime;

    @JsonProperty("parent_id")
    private long parentId;

    private boolean deleted;
    private String vote;
    private String platform;

    @JsonProperty("has_admin_badge")
    private boolean hasAdminBadge;
    private String[] children;
}
