package com.skillstorm.linkedinclone.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowDto {
    private String email1;
    private String email2;
    private boolean follow;
}
