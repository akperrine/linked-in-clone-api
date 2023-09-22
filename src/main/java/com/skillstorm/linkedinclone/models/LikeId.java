package com.skillstorm.linkedinclone.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class LikeId implements Serializable {
    private String email;
    private String postId;
}
