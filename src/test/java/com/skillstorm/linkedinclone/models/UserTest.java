package com.skillstorm.linkedinclone.models;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static com.mongodb.assertions.Assertions.*;

public class UserTest {

        @Test
        public void testAddFollowing() {
            User user1 = new User();
            User user2 = new User();
            user1.setFollowing(new HashSet<>());
            user1.addFollowing(user2);

            // Verify that user1 now follows user2
            assertTrue(user1.getFollowing().contains(user2));
        }

        @Test
        public void testRemoveFollowing() {
            User user1 = new User();
            User user2 = new User();
            user1.setFollowing(new HashSet<>());
            user1.addFollowing(user2);

            // Verify that user1 initially follows user2
            assertTrue(user1.getFollowing().contains(user2));

            user1.removeFollowing(user2);

            // Verify that user1 doesn't follow user2 anymore
            assertFalse(user1.getFollowing().contains(user2));
        }

        @Test
        public void testRemoveFollowingNonExistent() {
            User user1 = new User();
            user1.setFollowing(new HashSet<>());
            User user2 = new User();

            // Verify that user1 doesn't initially follow user2
            assertFalse(user1.getFollowing().contains(user2));

            user1.removeFollowing(user2);

            // Verify that user1 still doesn't follow user2 (no exception is thrown)
            assertFalse(user1.getFollowing().contains(user2));
        }
}
