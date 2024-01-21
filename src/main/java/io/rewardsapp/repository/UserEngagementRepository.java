package io.rewardsapp.repository;

import io.rewardsapp.domain.auth.User;
import io.rewardsapp.domain.educational.EducationalResource;
import io.rewardsapp.domain.educational.UserEngagement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEngagementRepository extends CrudRepository<UserEngagement, Long> {

    Optional<UserEngagement> findByUserIdAndEducationalResource(Long userId, EducationalResource educationalResource);

    Long countByEducationalResourceAndLikeStatus(EducationalResource resource, boolean status);

    Long countByEducationalResourceAndShareStatus(EducationalResource resource, boolean status);

    Long countByEducationalResourceAndSavedStatus(EducationalResource resource, boolean status);

    boolean existsByUserAndEducationalResourceAndLikeStatus(User user, EducationalResource resource, boolean status);

    boolean existsByUserAndEducationalResourceAndShareStatus(User user, EducationalResource resource, boolean status);

    boolean existsByUserAndEducationalResourceAndSavedStatus(User user, EducationalResource resource, boolean status);

}
