package io.rewardsapp.repository;

import io.rewardsapp.domain.auth.User;
import io.rewardsapp.domain.educational.EducationalResource;
import io.rewardsapp.domain.educational.UserEngagement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEngagementRepository extends CrudRepository<UserEngagement, Long> {

    Optional<UserEngagement> findByUserAndEducationalResource(User user, EducationalResource educationalResource);
}
