package org.example.iyakimovich.homework4.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

@Data
public class CommentDTO {
    @JsonProperty("data")
    private CommentsDataDTO[] commentData;

    private boolean success;
    private int status;

}
