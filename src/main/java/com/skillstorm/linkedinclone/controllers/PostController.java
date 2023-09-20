package com.skillstorm.linkedinclone.controllers;

import com.skillstorm.linkedinclone.dtos.PostDto;
import com.skillstorm.linkedinclone.models.Post;
import com.skillstorm.linkedinclone.services.PostService;
import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PostController {

    @Autowired
    private PostService postService;


    @GetMapping("/post/{postId}")
    public ResponseEntity<Post> findPostById(@PathVariable String postId){
        Post result = postService.findPostById(postId);

        if(result != null){
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/new")
    public ResponseEntity<Post> addNewPost(@RequestBody PostDto postData){
        Post newPost = postService.addNewPost(postData);

        return new ResponseEntity<>(newPost, HttpStatus.CREATED);
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<Post> updatePostById(@PathVariable String postId, @RequestBody PostDto postData){
        Post result = postService.updatePostById(postId, postData);

        if(result != null){
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Void> deletePostById(@PathVariable String postId){
        if(postService.deletePostById(postId)){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }
}
