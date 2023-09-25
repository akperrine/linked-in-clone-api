package com.skillstorm.linkedinclone.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowDto {
    private String emailOfUser;
    private String emailOfRequestedUser;
    private boolean follow;
}
