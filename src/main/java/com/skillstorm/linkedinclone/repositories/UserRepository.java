package com.skillstorm.linkedinclone.repositories;

import com.skillstorm.linkedinclone.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE UPPER(u.firstName) LIKE UPPER(CONCAT(:searchString, '%')) OR UPPER(u.lastName) LIKE UPPER(CONCAT(:searchString, '%'))")
    List<User> searchUsersByFirstNameOrLastName(@Param("searchString") String searchString);
}
