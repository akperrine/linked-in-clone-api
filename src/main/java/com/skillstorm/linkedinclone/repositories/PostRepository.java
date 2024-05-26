package com.skillstorm.linkedinclone.repositories;

import com.skillstorm.linkedinclone.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    Page<Post> findByEmailInOrderByTimestampDesc(List<String> emailList, Pageable pageable);
}
