package com.skillstorm.linkedinclone.controllers;

import com.skillstorm.linkedinclone.dtos.LikeDto;
import com.skillstorm.linkedinclone.dtos.PostDto;
import com.skillstorm.linkedinclone.models.Post;
import com.skillstorm.linkedinclone.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<List<Post>> findAllPosts(){
        List<Post> result = postService.findAllPosts();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


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

    @PostMapping("/like")
    public ResponseEntity<?> likePost(@RequestBody LikeDto likeDto){
        if(postService.likePost(likeDto)){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    //TODO mihgt need to update it to retrieve user email from Authentication object
    @GetMapping("/relevantPosts/{email}")
    public ResponseEntity<?> getRelevantPosts(@PathVariable String email, @RequestParam int batch){
        List<?> result = postService.getRelevantPosts(email, batch);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
