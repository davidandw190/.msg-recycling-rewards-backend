package io.rewardsapp.domain.educational;

import io.rewardsapp.domain.auth.User;

import java.io.Serializable;

public class UserEngagementId implements Serializable {
    private User user;
    private EducationalResource educationalResource;
}
