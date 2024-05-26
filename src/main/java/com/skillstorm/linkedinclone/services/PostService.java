package com.skillstorm.linkedinclone.services;

import com.skillstorm.linkedinclone.dtos.LikeDto;
import com.skillstorm.linkedinclone.dtos.PostDto;
import com.skillstorm.linkedinclone.models.Like;
import com.skillstorm.linkedinclone.models.LikeId;
import com.skillstorm.linkedinclone.models.Post;
import com.skillstorm.linkedinclone.models.User;
import com.skillstorm.linkedinclone.repositories.LikeRepository;
import com.skillstorm.linkedinclone.repositories.PostRepository;

import com.skillstorm.linkedinclone.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    private static final int DEFAULT_PAGE_SIZE = 4;

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
        //newPost.setId(newPost.hashCode());
        return postRepository.save(newPost);
    }

    public boolean deletePostById(String postId) {
        if(postRepository.findById(postId).isPresent()){
            postRepository.deleteById(postId);
            return true;
        }
        return false;
    }

    public List<Post> findAllPosts(){
        return postRepository.findAll();
    }

    public boolean likePost(LikeDto likeDto) {
        User user = userRepository.findByEmail(likeDto.getEmail()).orElse(null);
        Post post = postRepository.findById(likeDto.getPostId()).orElse(null);
        System.out.println(user+ " "+post);
        if(user != null && post != null){
            LikeId id = new LikeId(likeDto.getEmail(), likeDto.getPostId());
            if(likeDto.isLike()){
                // increment like
                if(!likeRepository.findById(id).isPresent()){
                    Like newLike = new Like(id);
                    likeRepository.save(newLike);
                }
            }else{
                // decrement like
                if(likeRepository.findById(id).isPresent()){
                    likeRepository.deleteById(id);
                }
            }
            post.setLikes(likeRepository.countByidPostId(likeDto.getPostId()));
            postRepository.save(post);
            return true;
        }
        return false;

    }

    public Map<String, Object> getRelevantPosts(String email, int batch) {
        List<String> userEmails = userRepository.findEmailsOfUsersFollowedByUserWithEmail(email);
        userEmails.add(email);
        //System.out.println(userEmails);

        Pageable pageable = PageRequest.of(batch, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "timeStamp"));

        Page<Post> posts = postRepository.findByEmailInOrderByTimestampDesc(userEmails, pageable);
        Map<String, Object> result = new HashMap<>();
        result.put("content", posts.getContent());
        result.put("totalItems", posts.getTotalElements());
        result.put("totalPages", posts.getTotalPages());
        result.put("currentPage", posts.getNumber());

        return result;

    }
}
