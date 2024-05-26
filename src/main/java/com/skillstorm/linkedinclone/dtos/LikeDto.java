package com.skillstorm.linkedinclone.dtos;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class LikeDto {
    private String email;
    private String postId;
    private boolean like;
}
