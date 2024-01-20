package io.rewardsapp.service;

import io.rewardsapp.domain.recycling.RecyclingCenter;
import io.rewardsapp.domain.auth.User;
import io.rewardsapp.domain.recycling.UserRecyclingActivity;
import io.rewardsapp.form.CreateRecyclingActivityForm;

import java.util.List;

public interface RecyclingActivityService {
    List<UserRecyclingActivity> getUserRecyclingActivitiesAtCenter(User user, RecyclingCenter center);

    int createActivity(CreateRecyclingActivityForm form);
}
