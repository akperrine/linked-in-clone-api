package com.skillstorm.linkedinclone.repositories;

import com.skillstorm.linkedinclone.models.Like;
import com.skillstorm.linkedinclone.models.LikeId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LikeRepository extends MongoRepository<Like, LikeId> {
    int countByidPostId(String postId);
}
