package com.skillstorm.linkedinclone.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document(collection = "likes")
@Data
@AllArgsConstructor
public class Like {
    @Id
    private LikeId id;
}
