package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.RecyclableMaterial;
import io.rewardsapp.domain.RecyclingCenter;
import io.rewardsapp.domain.User;
import io.rewardsapp.domain.UserRecyclingActivity;
import io.rewardsapp.form.CreateRecyclingActivityForm;
import io.rewardsapp.repository.CenterRepository;
import io.rewardsapp.repository.MaterialsRepository;
import io.rewardsapp.repository.UserRecyclingActivityRepository;
import io.rewardsapp.repository.UserRepository;
import io.rewardsapp.service.RecyclingActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecyclingActivityServiceImpl implements RecyclingActivityService {
    private final UserRecyclingActivityRepository activityRepository;
    private final CenterRepository centerRepository;
    private final UserRepository<User> userRepository;
    private final MaterialsRepository materialsRepository;

    @Override
    public List<UserRecyclingActivity> getUserRecyclingActivitiesAtCenter(User user, RecyclingCenter center) {
        return activityRepository.findAllByUserAndRecyclingCenter(user, center);
    }

    @Override
    public void createActivity(CreateRecyclingActivityForm form) {

        User user = userRepository.get(form.userId());

        RecyclingCenter recyclingCenter = centerRepository.findById(form.centerId())
                .orElseThrow(() -> new IllegalArgumentException("Recycling center not found"));

        RecyclableMaterial material = materialsRepository.findById(form.materialId())
                .orElseThrow(() -> new IllegalArgumentException("Recyclable material not found"));

        UserRecyclingActivity activity = UserRecyclingActivity.builder()
                .user(user)
                .recyclingCenter(recyclingCenter)
                .recycledMaterial(material)
                .amount(form.amount())
                .createdAt(LocalDateTime.now())
                .build();

        activityRepository.save(activity);
    }
}
