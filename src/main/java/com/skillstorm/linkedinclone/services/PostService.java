package com.skillstorm.linkedinclone.services;

import com.skillstorm.linkedinclone.dtos.PostDto;
import com.skillstorm.linkedinclone.models.Post;
import com.skillstorm.linkedinclone.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Post updatePostById(String postId, PostDto postData) {
        Post post = postRepository.findById(postId).orElse(null);

        if(post!=null){
            post.setContent(postData.getContent());
            post.setLikes(postData.getLikes());
            post.setComments(postData.getComments());
            post.setTimestamp(new Date());
            return postRepository.save(post);
        }
        return post;

    }

    public Post findPostById(String postId) {
        Post post = postRepository.findById(postId).orElse(null);
        return post;
    }

    public Post addNewPost(PostDto postData) {
        Post newPost = postData.toPost();
        newPost.setTimestamp(new Date());
        return postRepository.save(newPost);
    }

    public boolean deletePostById(String postId) {
        if(postRepository.findById(postId).isPresent()){
            postRepository.deleteById(postId);
            return true;
        }
        return false;
    }
}
