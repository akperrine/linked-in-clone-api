package com.skillstorm.linkedinclone.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowDto {
    private String userEmail;
    private String connectionEmail;
    private boolean follow;
}
