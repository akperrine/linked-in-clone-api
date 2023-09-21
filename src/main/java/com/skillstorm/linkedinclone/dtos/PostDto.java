package com.skillstorm.linkedinclone.dtos;

import com.skillstorm.linkedinclone.models.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostDto {
    private String email;
    private String content;
    private int likes;
    private String[] comments;

    public Post toPost(){
        Post newPost = new Post();
        newPost.setEmail(email);
        newPost.setContent(content);
        newPost.setLikes(likes);
        newPost.setComments(comments);

        return newPost;
    }
}
