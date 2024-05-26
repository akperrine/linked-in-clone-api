package com.skillstorm.linkedinclone.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.ZonedDateTime;
import java.util.Date;

@Document(collection = "posts")
@Data
@NoArgsConstructor
public class Post {

    @Id
    private String id;
    private String email;
    //TODO might need to change this to support image content
    private String content;
    private Date timestamp;
    private int likes;
    private String[] comments;

    public Post(String email, String content, int likes, String[] comments) {
        this.email = email;
        this.content = content;
        this.likes = likes;
        this.comments = comments;
    }
}
