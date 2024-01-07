package io.rewardsapp.service;

import io.rewardsapp.domain.RecyclingCenter;
import io.rewardsapp.domain.User;
import io.rewardsapp.domain.UserRecyclingActivity;
import io.rewardsapp.form.CreateRecyclingActivityForm;

import java.util.List;

public interface RecyclingActivityService {
    List<UserRecyclingActivity> getUserRecyclingActivitiesAtCenter(User user, RecyclingCenter center);

    boolean createActivity(CreateRecyclingActivityForm form);
}
