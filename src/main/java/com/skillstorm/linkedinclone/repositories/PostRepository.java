package com.skillstorm.linkedinclone.repositories;

import com.skillstorm.linkedinclone.models.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

}
