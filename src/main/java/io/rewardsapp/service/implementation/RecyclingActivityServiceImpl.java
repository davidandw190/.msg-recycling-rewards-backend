package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.RecyclingCenter;
import io.rewardsapp.domain.User;
import io.rewardsapp.domain.UserRecyclingActivity;
import io.rewardsapp.repository.UserRecyclingActivityRepository;
import io.rewardsapp.service.RecyclingActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecyclingActivityServiceImpl implements RecyclingActivityService {
    private final UserRecyclingActivityRepository activityRepository;

    @Override
    public List<UserRecyclingActivity> getUserRecyclingActivitiesAtCenter(User user, RecyclingCenter center) {
        return activityRepository.findAllByUserAndRecyclingCenter(user, center);
    }
}
